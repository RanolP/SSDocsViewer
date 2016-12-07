package me.ranol.docviewer;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.net.URI;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import me.ranol.docviewer.doc.Document;
import me.ranol.docviewer.doc.Document.DocOptions;
import me.ranol.docviewer.swt.MessageView;

public class DocumentEntry extends Composite {
	private Document doc = null;
	private Table table;
	Label spellName = new Label(this, SWT.NONE);
	private static final int MARGIN = 3;
	Button showExtends = new Button(this, SWT.CHECK);

	public DocumentEntry(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		GridData temp = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		temp.heightHint = 39;
		temp.widthHint = 331;

		spellName.setFont(SWTResourceManager.getFont("맑은 고딕", 15, SWT.BOLD | SWT.ITALIC));
		spellName.setLayoutData(temp);
		spellName.setText("Spell Name will here.");
		showExtends.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateEntry();
			}
		});

		showExtends.setText("상속 옵션 보이기");

		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (doc == null || (e.stateMask & SWT.CTRL) != SWT.CTRL) {
					return;
				}
				DocOptions o = doc.getOptions()
					.get(table.getSelectionIndex());
				if (!o.hasSpigotDocs()) {
					return;
				}
				if (Desktop.isDesktopSupported()) {
					Desktop d = Desktop.getDesktop();
					if (d != null && d.isSupported(Action.BROWSE)) {
						try {
							d.browse(new URI(o.getSpigotDocs()));
						} catch (Exception ex) {
							MessageView.error(getShell())
								.title("오류!")
								.message("Spigot Doc 링크를 열 수 없습니다.")
								.open();
						}
					}
				}
			}
		});
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		table.setHeaderVisible(false);
		table.setLinesVisible(true);
		table.addListener(SWT.EraseItem, e -> e.detail &= ~SWT.FOREGROUND);
		table.addListener(SWT.MeasureItem, e -> {
			TableItem i = (TableItem) e.item;
			String txt = i.getText(e.index);
			Point size = e.gc.textExtent(txt);
			e.width = size.x + 2 * MARGIN;
			e.height = Math.max(e.height, size.y + MARGIN);
		});
		table.addListener(SWT.PaintItem, e -> {
			TableItem i = (TableItem) e.item;
			String[] txt = i.getText(e.index)
				.split("\n");
			int y = 0;
			Point size = e.gc.textExtent(txt[0]);
			if (e.index == 1) {
				y = Math.max(0, (e.height - size.y) / 2);
			}
			for (String s : txt) {
				if (s.startsWith("[⬍]")) {
					e.gc.setForeground(Display.getDefault()
						.getSystemColor(SWT.COLOR_BLUE));
				} else {
					e.gc.setForeground(Display.getDefault()
						.getSystemColor(SWT.COLOR_BLACK));
				}
				e.gc.drawText(s, e.x + MARGIN, e.y + y, true);
				y += size.y;
			}
		});
		updateEntry();
	}

	public void setDocument(Document doc) {
		this.doc = doc;
		updateEntry();
	}

	public Document getDocument() {
		return doc;
	}

	public void updateEntry() {
		table.clearAll();
		table.redraw();
		if (doc == null) {
			spellName.setText("선택되지 않음");
			return;
		}
		spellName.setText(doc.getSpellName());
		table.setItemCount(doc.getOptions()
			.size());
		int idx = 0;
		int items = doc.getOptions()
			.size();
		for (DocOptions o : doc.getOptions()) {
			if (!showExtends.getSelection() && o.hasFrom()) {
				items--;
				continue;
			}
			TableItem i = table.getItem(idx++);
			String text = new StringBuilder().append("")
				.append(o.getName())
				.append(" - ")
				.append(o.getDescription())
				.append(o.hasSpigotDocs() ? "[Ctrl + Click하여 Doc으로 이동]" : "")
				.append(o.hasFrom() ? "\n[⬍] " + o.getFrom() + "에서 상속됨" : "")
				.toString();
			if (o.hasValues()) {
				text += "\n" + o.getValues()
					.stream()
					.collect(Collectors.joining(", "));
			}
			i.setText(text);
		}
		table.setItemCount(items);
	}

}

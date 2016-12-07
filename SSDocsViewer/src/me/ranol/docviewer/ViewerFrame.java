package me.ranol.docviewer;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import me.ranol.docviewer.doc.Document;
import me.ranol.docviewer.doc.Documents;

public class ViewerFrame {

	public static Shell shell;
	private Table table;
	private DocumentEntry entry;

	public static void main(String[] args) {
		ViewerFrame window = new ViewerFrame();
		window.open();
	}

	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	void renderDocs() {
		table.clearAll();
		table.setItemCount(Documents.size());
		int idx = 0;
		for (Document d : Documents.real()) {
			TableItem i = table.getItem(idx++);
			i.setText(d.getSpellName());
		}
	}

	protected void createContents() {
		shell = new Shell();
		shell.setSize(700, 500);
		shell.setText("SSDocsViewer");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(shell, SWT.NONE);
		sashForm.setLocation(0, 0);

		table = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				entry.setDocument(Documents.get(table.getSelectionIndex()));
			}
		});
		table.setHeaderVisible(false);
		table.setLinesVisible(true);

		entry = new DocumentEntry(sashForm, SWT.NONE);
		sashForm.setWeights(new int[] { 1, 3 });

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem file = new MenuItem(menu, SWT.NONE);
		file.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				dialog.setText("Json 파일 열기");
				dialog.setFilterPath(System.getProperty("user.dir"));
				dialog.setFilterExtensions(new String[] { "*.json", "*.*" });
				String select = dialog.open();
				Documents.parse(new File(select));
				renderDocs();
			}
		});
		file.setToolTipText("");
		file.setText("불러오기");
	}
}

package me.ranol.docviewer.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import me.ranol.docviewer.doc.Document.DocOptions;

public enum Documents {
	INSTANCE;
	List<Document> docs = new ArrayList<>();

	public static Document getByName(String name) {
		Document result = null;
		for (Document d : INSTANCE.docs) {
			if (d.getSpellName()
				.equals(name)) result = d;
		}
		return result;
	}

	public static int size() {
		return INSTANCE.docs.size();
	}

	public static List<Document> real() {
		return new ArrayList<>(INSTANCE.docs);
	}

	public static Document get(int i) {
		return INSTANCE.docs.get(i);
	}

	public static void parse(File file) {
		INSTANCE.docs = DocumentParser.parseAll(file);
		for (Document d : INSTANCE.docs) {
			if (d.superClasses.isEmpty()) {
				continue;
			}
			for (String s : d.superClasses) {
				Document sup = getByName(s);
				if (sup == null) {
					continue;
				}
				List<DocOptions> options = new ArrayList<>(sup.getOptions());
				ListIterator<DocOptions> it = options.listIterator();
				while (it.hasNext()) {
					DocOptions o = it.next();
					if (d.options.contains(o)) {
						it.remove();
					}
					if (o.hasFrom()) {
						continue;
					}
					DocOptions copy = new DocOptions(o);
					copy.from = s;
					d.options.add(copy);
				}
			}
		}
	}
}

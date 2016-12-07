package me.ranol.docviewer.doc;

import java.util.ArrayList;
import java.util.List;

public class Document {
	List<String> superClasses = new ArrayList<>();
	List<DocOptions> options = new ArrayList<>();
	String description = "null";
	String name;

	public static class DocOptions {
		String description = "null";
		List<String> values = new ArrayList<>();
		String docLink = "";
		String name = "null";
		String from = "";

		public DocOptions(DocOptions parent) {
			this.description = parent.description;
			this.values.addAll(parent.values);
			this.docLink = parent.docLink;
			this.name = parent.name;
			this.from = parent.from;
		}

		public DocOptions() {
		}

		public String getDescription() {
			return description;
		}

		public List<String> getValues() {
			return values;
		}

		public String getSpigotDocs() {
			return docLink;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "DocOptions [name=" + name + ", desc=" + description
					+ (hasSpigotDocs() ? ", spigotDocs=" + docLink : "")
					+ (hasValues() ? ", values=" + values.toString() : "") + "]";
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof DocOptions && ((DocOptions) obj).name.equals(name);
		}

		public boolean hasSpigotDocs() {
			return !docLink.isEmpty();
		}

		public boolean hasValues() {
			return !values.isEmpty();
		}

		public boolean hasFrom() {
			return !from.isEmpty();
		}

		public String getFrom() {
			return from;
		}
	}

	public String getSpellName() {
		return name;
	}

	public List<DocOptions> getOptions() {
		return options;
	}
}

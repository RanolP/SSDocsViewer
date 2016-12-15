package me.ranol.docviewer.doc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import me.ranol.docviewer.ViewerFrame;
import me.ranol.docviewer.doc.Document.DocOptions;
import me.ranol.docviewer.swt.MessageView;

public class DocumentParser {
	public static Document parse(String name, JsonObject o) {
		Document doc = new Document();
		doc.name = name;
		if (o.has("doc") && o.get("doc")
			.isJsonPrimitive()) {
			doc.description = o.getAsJsonPrimitive("doc")
				.getAsString();
		}
		if (o.has("super") && o.get("super")
			.isJsonArray()) {
			Iterator<JsonElement> e = o.getAsJsonArray("super")
				.iterator();
			while (e.hasNext()) {
				doc.superClasses.add(e.next()
					.getAsString());
			}
		}
		for (Entry<String, JsonElement> entry : o.entrySet()) {
			if (entry.getKey()
				.equals("doc")
					|| entry.getKey()
						.equals("super"))
				continue;
			DocOptions option = new DocOptions();
			option.name = entry.getKey();
			if (entry.getValue()
				.isJsonObject()) {
				JsonObject temp = entry.getValue()
					.getAsJsonObject();
				if (temp.has("doc") && temp.get("doc")
					.isJsonPrimitive()) {
					option.description = temp.getAsJsonPrimitive("doc")
						.getAsString();
				}
				if (temp.has("values") && temp.get("values")
					.isJsonArray()) {
					Iterator<JsonElement> e = temp.getAsJsonArray("values")
						.iterator();
					while (e.hasNext())
						option.values.add(e.next()
							.getAsString());
				}
				if (temp.has("linkDocs") && temp.get("linkDocs")
					.isJsonPrimitive()) {
					option.docLink = temp.getAsJsonPrimitive("spigotDoc")
						.getAsString();
				}
			}
			doc.options.add(option);
		}
		return doc;
	}

	public static List<Document> parseAll(File file) {
		List<Document> result = new ArrayList<>();
		MessageView v = MessageView.error(ViewerFrame.shell)
			.title("오류!");
		try {
			JsonObject o = new JsonParser().parse(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8))
				.getAsJsonObject();
			o.entrySet()
				.forEach(e -> {
					if (e.getValue()
						.isJsonObject())
						result.add(parse(e.getKey(), e.getValue()
							.getAsJsonObject()));
				});
		} catch (JsonSyntaxException e) {
			v.message("Json 문법이 아닌 파일입니다.")
				.open();
		} catch (FileNotFoundException e) {
			v.message("파일이 존재하지 않습니다.")
				.open();
		} catch (UnsupportedEncodingException e1) {
			v.message("호환되는 인코딩이 아닙니다.")
				.open();
		} catch (JsonIOException | IOException e) {
			v.message("파일을 여는 도중 문제가 발생했습니다.")
				.open();
		}
		return result;
	}
}

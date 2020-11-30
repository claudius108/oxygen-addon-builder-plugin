package ro.kuberam.oxygen.addonBuilder.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

import ro.kuberam.oxygen.addonBuilder.AddonBuilderPluginExtension;

public class IOUtilities {

	public static void storeJavaTemplateToBeDeleted(String addonPackageAbsolutePath, String javaTemplateName,
			String addonJavaPackageName) {
		String javaTemplate = null;
		try {
			javaTemplate = new String(Files.readAllBytes(Paths
					.get(AddonBuilderPluginExtension.class.getResource("templates/java/" + javaTemplateName).toURI())),
					StandardCharsets.UTF_8);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		javaTemplate = javaTemplate.replaceAll("addon-javapackage-name", addonJavaPackageName);

		try {
			Files.write(Paths.get(addonPackageAbsolutePath, javaTemplateName), javaTemplate.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void storeJavaTemplates(File javaTemplatesDir, String addonJavaPackageName, File targetDir) {
		File[] javaTemplateFiles = javaTemplatesDir.listFiles();

		for (File javaTemplateFile : javaTemplateFiles) {
			String javaTemplate = null;
			try {
				javaTemplate = new String(Files.readAllBytes(javaTemplateFile.toPath()), StandardCharsets.UTF_8);

			} catch (IOException e2) {
				e2.printStackTrace();
			}
			javaTemplate = javaTemplate.replaceAll("addon-javapackage-name", addonJavaPackageName);

			try {
				Files.write(targetDir.toPath().resolve(javaTemplateFile.getName()), javaTemplate.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String serializeObjectToFile(File folderPath, Object object, String objectName)
			throws FileNotFoundException, IOException {
		String filePath = folderPath + File.separator + objectName + ".ser";
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath));
		oos.writeObject(object);
		oos.flush();
		oos.close();

		return filePath;
	}

	public static Object deserializeObjectFromFile(String filePath)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		Object result = null;
		ObjectInputStream ois = new ObjectInputStream(IOUtilities.class.getResourceAsStream(filePath));
		result = ois.readObject();
		ois.close();

		return result;
	}

	public static Object deserializeObjectFromFile(File file)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		Object result = null;
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		result = ois.readObject();
		ois.close();

		return result;
	}

	public static void saveXmlToFile(Element documentElement, File outputFile) {
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(outputFile);
			Source input = new DOMSource(documentElement);
			transformer.transform(input, output);
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e1) {
			e1.printStackTrace();
		} catch (TransformerException e1) {
			e1.printStackTrace();
		}

	}

}

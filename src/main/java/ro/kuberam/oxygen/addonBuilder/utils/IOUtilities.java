package ro.kuberam.oxygen.addonBuilder.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Element;

import ro.kuberam.oxygen.addonBuilder.AddonBuilderPluginExtension;

public class IOUtilities {

	public static void storeJavaTemplateToBeDeleted(String addonPackageAbsolutePath,
			String javaTemplateName, String addonJavaPackageName) {
		String javaTemplate = null;
		try {
			javaTemplate = IOUtils.toString(AddonBuilderPluginExtension.class
					.getResourceAsStream("templates/java/" + javaTemplateName));
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		javaTemplate = javaTemplate.replaceAll("addon-javapackage-name", addonJavaPackageName);

		try {
			FileUtils.writeStringToFile(new File(addonPackageAbsolutePath + File.separator
					+ javaTemplateName), javaTemplate);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void storeJavaTemplates(File javaTemplatesDir, String addonJavaPackageName, File targetDir) {
		File[] javaTemplateFiles = javaTemplatesDir.listFiles();

		for (File javaTemplateFile : javaTemplateFiles) {
			String javaTemplate = null;
			try {
				javaTemplate = IOUtils.toString(new FileInputStream(javaTemplateFile));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			javaTemplate = javaTemplate.replaceAll("addon-javapackage-name", addonJavaPackageName);

			try {
				FileUtils.writeStringToFile(
						new File(targetDir + File.separator + javaTemplateFile.getName()), javaTemplate);
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

	public static Object deserializeObjectFromFile(String filePath) throws FileNotFoundException,
			IOException, ClassNotFoundException {
		Object result = null;
		ObjectInputStream ois = new ObjectInputStream(IOUtilities.class.getResourceAsStream(filePath));
		result = ois.readObject();
		ois.close();

		return result;
	}

	public static Object deserializeObjectFromFile(File file) throws FileNotFoundException, IOException,
			ClassNotFoundException {
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

package cucumber.runtime.io;

import cucumber.runtime.CucumberException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class Helpers {
    private Helpers() {
    }

    static boolean hasSuffix(String suffix, String name) {
        return suffix == null || name.endsWith(suffix);
    }

    static String filePath(URL fileUrl) {
        if (!"file".equals(fileUrl.getProtocol())) {
            /*
                The original cucumber code would thrown the comment out exception. This would
                happen when trying to run the application from a webstart application, as the
                protocol here would be http.
             */
            //throw new CucumberException("Expected a file URL:" + fileUrl.toExternalForm());

            /*
                Instead we find the local JAR file and look inside it
             */
            try {
				final File localJar = new File(
					Helpers.class
						.getProtectionDomain()
						.getCodeSource()
						.getLocation()
						.toURI().getPath());

                fileUrl = new File(System.getProperty("LocalIridiumJARFile")).toURL();
            } catch (Exception e) {
                throw new CucumberException(e);
            }
        }
        try {
            return fileUrl.toURI().getSchemeSpecificPart();
        } catch (URISyntaxException e) {
            throw new CucumberException(e);
        }
    }

    static String jarFilePath(URL jarUrl) {
        String urlFile = jarUrl.getFile();

        int separatorIndex = urlFile.indexOf("!/");
        if (separatorIndex == -1) {
            throw new CucumberException("Expected a jar URL: " + jarUrl.toExternalForm());
        }
        try {
            URL fileUrl = new URL(urlFile.substring(0, separatorIndex));
            return filePath(fileUrl);
        } catch (MalformedURLException e) {
            throw new CucumberException(e);
        }
    }
}

package org.safehaus.chop.webapp.service;

import com.google.inject.Inject;
import org.safehaus.chop.api.ProviderParams;
import org.safehaus.chop.webapp.dao.ProviderParamsDao;
import org.safehaus.chop.webapp.service.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

public class KeyService {

    private static final Logger LOG = LoggerFactory.getLogger(KeyService.class);

    private static final String CONFIG_FILE = "chop-ui.properties";
    private static final String CONFIG_KEY = "key.files.dir";

    @Inject
    private ProviderParamsDao providerParamsDao = null;

    private String keyFilesDir;

    private String getKeyFilesDir() {

        if (keyFilesDir == null) {
            keyFilesDir = FileUtil.readProperties(CONFIG_FILE).getProperty(CONFIG_KEY);
        }

        return keyFilesDir;
    }

    public File addFile(String username, String keyPairName, String fileName) throws FileNotFoundException {

        String dir = getKeyFilesDir() + "/" + username;
        String filePath = dir + "/" + fileName;

        addKeyFile(username, keyPairName, filePath);

        new File(dir).mkdirs();
        return new File(filePath);
    }

    private void addKeyFile(String username, String keyPairName, String filePath) {

        ProviderParams params = providerParamsDao.getByUser(username);
        params.getKeys().put(keyPairName, filePath);

        save(params);
    }

    private void save(ProviderParams params) {
        try {
            providerParamsDao.save(params);
        } catch (Exception e) {
            LOG.error("Error to save key file: ", e);
        }
    }

    public Map<String, String> getKeys(String username) {
        ProviderParams params = providerParamsDao.getByUser(username);
        return params.getKeys();
    }

    public void removeKey(String username, String keyName) {

        ProviderParams params = providerParamsDao.getByUser(username);

        String filePath = params.getKeys().get(keyName);
        new File(filePath).delete();

        params.getKeys().remove(keyName);

        save(params);
    }

}

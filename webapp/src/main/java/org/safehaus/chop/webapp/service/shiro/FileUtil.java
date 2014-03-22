/*
 * Copyright 2014 dilshat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.safehaus.chop.webapp.service.shiro;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

    private static String getJarLocation() throws URISyntaxException {
//        Path path = Paths.get(FileUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI());
//        return path.getParent().toString();
        return null;
    }

    /**
     * user1;password1;role1;path_to_cert
     * user2;password2;role2,role3;path_to_cert
     *
     * @param name
     * @return
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    public static List<String> readFile(String name) {
        List<String> content = null;
        try {
            content = Files.readLines(new File(getJarLocation() + "/" + name), Charsets.UTF_8);
        } catch (FileNotFoundException e) {
            String tempContent = "user;pass;user;location";
            writeFile(name, tempContent);

            content = Arrays.asList(tempContent);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void writeFile(String name, String content){
        try {
            Files.write(content, new File(getJarLocation() + "/" + name), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}

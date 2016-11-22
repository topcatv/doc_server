package org.topcat.docserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by topcat on 2016/11/21.
 */
public interface WordTemplate {

    void fromTemplate(InputStream input, OutputStream output, Map<String, String> context);
}

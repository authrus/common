package com.authrus.common.ssl;

import javax.net.ssl.SSLContext;

public interface Certificate {
   SSLContext getContext();
   String[] getProtocols();
   String[] getCipherSuites();
}

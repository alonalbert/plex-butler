package com.alonalbert.plexbutler.plex;

import android.util.Log;

import com.alonalbert.plexbutler.plex.model.Server;

import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

/**
 * Convert an XML Response to a Plex Server Array. Plex Get Servers request does not support JSON
 * for some reason so weed to parse the XML.
 */
public class PlexServersXmlConverter extends AbstractXmlHttpMessageConverter {
  private static final String TAG = "PlexButler";
  private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

  @Override
  protected Object readFromSource(Class clazz, HttpHeaders headers, Source source) throws IOException {
    final StreamSource streamSource = (StreamSource) source;
    try {
      final Document document = factory.newDocumentBuilder().parse(streamSource.getInputStream());
      final NodeList serverElements = document.getElementsByTagName("Server");
      final int n = serverElements.getLength();
      final Server[] servers = new Server[n];
      for (int i = 0; i < n; i++) {
        final Element serverElement = (Element) serverElements.item(i);
        servers[i] =  new Server(
          serverElement.getAttribute("name"),
          serverElement.getAttribute("address"),
          Integer.valueOf(serverElement.getAttribute("port")));
      }
      return servers;
    } catch (SAXException | ParserConfigurationException e) {
      Log.e(TAG, "Error converting getServers response", e);
      return new Server[0];
    }
  }

  @Override
  protected void writeToResult(Object o, HttpHeaders headers, Result result) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  protected boolean supports(Class clazz) {
    return clazz.equals(Server[].class);
  }

}

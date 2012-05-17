package de.faustedition.document;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import de.faustedition.FaustAuthority;
import de.faustedition.FaustURI;
import de.faustedition.graph.FaustGraph;
import de.faustedition.xml.CustomNamespaceMap;
import de.faustedition.xml.XMLStorage;
import de.faustedition.xml.XMLUtil;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

@Component
public class ArchiveInitializer implements InitializingBean {
	public static final FaustURI ARCHIVE_DESCRIPTOR_URI = new FaustURI(FaustAuthority.XML, "/archives.xml");

	private static final Logger LOG = LoggerFactory.getLogger(ArchiveInitializer.class);

	@Autowired
	private FaustGraph graph;

	@Autowired
	private XMLStorage xml;

	@Autowired
	private GraphDatabaseService db;

	@Autowired
	private TransactionTemplate transactionTemplate;

	public void createArchives() {
		LOG.info("Adding archive data");
		try {
			final ArchiveCollection archives = graph.getArchives();
			XMLUtil.saxParser().parse(xml.getInputSource(ARCHIVE_DESCRIPTOR_URI), new DefaultHandler() {
				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					if ("archive".equals(localName) && CustomNamespaceMap.FAUST_NS_URI.equals(uri)) {
						final Archive archive = new Archive(db.createNode(), Preconditions.checkNotNull(attributes.getValue("id")));
						if (LOG.isDebugEnabled()) {
							LOG.debug("Adding {}", archive);
						}
						archives.add(archive);
					}
				}
			});
		} catch (SAXException e) {
			throw Throwables.propagate(e);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				if (graph.getArchives().isEmpty()) {
					createArchives();
				}
			}
		});
	}
}

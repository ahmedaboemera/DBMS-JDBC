import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public interface DBMS_I
{
	void createDB(String name) throws ParserConfigurationException,
			TransformerException, IOException;

	void useDB(String name) throws IOException;

	void createTable(String name, String[] attrs, int[] types)
			throws ParserConfigurationException, TransformerException,
			JAXBException, IOException, SAXException;

	void insertIntoTable(String tablename, String[] attrs, String[] data)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerException;

	void delete(String tablename, String attrCondition, String dataCondition,
			String comparator2) throws ParserConfigurationException,
			SAXException, IOException, TransformerException;

	void select(String tablename, String[] selected, String attrCondition,
			String dataCondition, String comparator2)
			throws ParserConfigurationException, SAXException, IOException;

	void update(String tablename, String attrCondition, String dataCondition,
			String comparator2, String[] attrs, String[] data)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerException;
}

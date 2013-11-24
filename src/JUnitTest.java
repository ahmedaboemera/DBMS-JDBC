import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class JUnitTest
{
	@Test
	public void testFindType1() throws ParserConfigurationException, TransformerException, SAXException, IOException, JAXBException
	{
		Parser tester = new Parser("use newDatabase ;");
	    assertEquals("Wrong Answer", 5, tester.findType());
	}
	@Test
	public void testFindType2() throws ParserConfigurationException, TransformerException, SAXException, IOException, JAXBException
	{
		Parser tester = new Parser("create table newTable (name string,age integer) ;");
	    assertEquals("Wrong Answer", 1, tester.findType());
	}
	@Test
	public void testFindType3() throws ParserConfigurationException, TransformerException, SAXException, IOException, JAXBException
	{
		Parser tester = new Parser("create database newDatabase ;");
	    assertEquals("Wrong Answer", 0, tester.findType());
	}
	@Test
	public void testFindType4() throws ParserConfigurationException, TransformerException, SAXException, IOException, JAXBException
	{
		Parser tester = new Parser("insert into newTable (name,age) values (omar,20) ;");
	    assertEquals("Wrong Answer", 6, tester.findType());
	}
	@Test
	public void testFindType5() throws ParserConfigurationException, TransformerException, SAXException, IOException, JAXBException
	{
		Parser tester = new Parser("update newTable set name = omar where age = 20 ;");
	    assertEquals("Wrong Answer", 2, tester.findType());
	}
	@Test
	public void testFindType6() throws ParserConfigurationException, TransformerException, SAXException, IOException, JAXBException
	{
		Parser tester = new Parser("select name,age from newTable where age = 20 ;");
	    assertEquals("Wrong Answer", 3, tester.findType());
	}
	@Test
	public void testFindType7() throws ParserConfigurationException, TransformerException, SAXException, IOException, JAXBException
	{
		Parser tester = new Parser("select * from newTable where age = 20 ;");
	    assertEquals("Wrong Answer", 3, tester.findType());
	}
	@Test
	public void testFindType8() throws ParserConfigurationException, TransformerException, SAXException, IOException, JAXBException
	{
		Parser tester = new Parser("delete from newTable where age = 20 ;");
	    assertEquals("Wrong Answer", 4, tester.findType());
	}
	@Test
	public void testFindType9() throws ParserConfigurationException, TransformerException, SAXException, IOException, JAXBException
	{
		Parser tester = new Parser("anyThingInvalid");
	    assertEquals("Wrong Answer", -1, tester.findType());
	}
}

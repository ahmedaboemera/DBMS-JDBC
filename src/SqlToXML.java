import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SqlToXML implements DBMS_I
{
	// private static String path = "/home/ahmedaboemera/";
	private static String path;
	private static String dbName;

	public static void setPath(String a)
	{
		path = a;
	}

	private static boolean checkFields(String name, String[] fields)
			throws ParserConfigurationException, SAXException, IOException
	{
		// File fXmlFile = new File(path + dbName + "/Schemas");
		File fXmlFile = new File(path + dbName + "\\Schemas");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc2 = dBuilder.parse(fXmlFile);

		doc2.getDocumentElement().normalize();
		NodeList list1 = doc2.getElementsByTagName("schemas");
		NodeList list2 = list1.item(0).getChildNodes();
		for (int i = 0; i < list2.getLength(); i++)
		{
			if (name.equalsIgnoreCase(list2.item(i).getNodeName()))
			{
				NodeList nlist3 = list2.item(i).getChildNodes().item(0)
						.getChildNodes();
				for (int j = 0; j < fields.length; j++)
				{
					boolean found = false;
					for (int j2 = 0; j2 < nlist3.getLength(); j2++)
					{
						if (fields[j].equalsIgnoreCase(nlist3.item(j2)
								.getNodeName()))
							found = true;
					}
					if (!found)
						return false;

				}
				break;
			}
		}
		return true;
	}

	private static boolean checkTable(String name)
			throws ParserConfigurationException, SAXException, IOException
	{
		if (dbName == null)
		{
			System.out.println("Database not selected");
			return false;
		}
		// File fXmlFile = new File(path + dbName + "/Schemas");
		File fXmlFile = new File(path + dbName + "\\Schemas");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc2 = dBuilder.parse(fXmlFile);

		doc2.getDocumentElement().normalize();
		NodeList list1 = doc2.getElementsByTagName("schemas");
		NodeList list2 = list1.item(0).getChildNodes();
		for (int i = 0; i < list2.getLength(); i++)
		{
			Node a = list2.item(i);
			if (a.getNodeName().equals(name))
				return true;
		}
		// System.out.println("table not found");
		return false;
	}

	private static boolean checkDB(String name) throws IOException
	{
		String current;
		BufferedReader reading = new BufferedReader(new FileReader(
				"dataBases.txt"));
		while ((current = reading.readLine()) != null)
		{
			if (current.contains("," + name + ","))
			{
				reading.close();
				return true;
			}
		}
		reading.close();
		return false;
	}

	private static void write(String name) throws IOException
	{
		BufferedReader reading = new BufferedReader(new FileReader(
				"dataBases.txt"));
		String current = "";
		String append;
		while ((append = reading.readLine()) != null)
		{
			current += append;
			// System.out.println(current);
		}
		current += "," + name + ",";
		PrintWriter pw = new PrintWriter("dataBases.txt", "UTF-8");
		pw.write(current);
		pw.close();
		reading.close();

	}

	public void createDB(String name) throws ParserConfigurationException,
			TransformerException, IOException
	{

		if (checkDB(name))
		{
			System.out.println("Database already exits");
			return;
		}
		write(name);
		File newFolder = new File(path + name);
		newFolder.mkdir();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element root = doc.createElement("schemas");
		doc.appendChild(root);
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		// StreamResult result = new StreamResult(new File(path + name + "/"
		// + "Schemas"));
		StreamResult result = new StreamResult(new File(path + name + "\\"
				+ "Schemas"));
		transformer.transform(source, result);
		System.out.println("Database Created Successfully!");
	}

	public void useDB(String name) throws IOException
	{
		if (checkDB(name))
		{
			dbName = name;
			System.out.println("Database is in use");
		}
		else
			System.out.println("Database not found");
	}

	public void createTable(String name, String[] attrs, int[] types)
			throws ParserConfigurationException, TransformerException,
			JAXBException, IOException, SAXException
	{
		if (dbName != null)
		{
			if (checkTable(name) == false)
			{
				// File newFolder = new File(path + dbName + "/" + name);
				File newFolder = new File(path + dbName + "\\" + name);
				newFolder.mkdir();
				DocumentBuilderFactory dF = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dbb = dF.newDocumentBuilder();
				// Document doc1 = dbb
				// .parse(new File(path + dbName + "/" + "Schemas"));
				Document doc1 = dbb.parse(new File(path + dbName + "\\"
						+ "Schemas"));
				Element table = doc1.createElement(name);
				doc1.getDocumentElement().appendChild(table);
				DocumentBuilderFactory docFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				// root elements
				Document doc = docBuilder.newDocument();
				Element parentnode = doc.createElement("Table");
				doc.appendChild(parentnode);
				Element rootElement = doc.createElement("types");
				parentnode.appendChild(rootElement);
				Element schemaRoot = doc1.createElement("Types");
				table.appendChild(schemaRoot);
				for (int i = 0; i < attrs.length; i++)
				{
					Element mytypes = doc.createElement(attrs[i].toLowerCase());
					Element mytypes1 = doc1.createElement(attrs[i]
							.toLowerCase());

					mytypes.appendChild(doc.createTextNode(Integer
							.toString(types[i])));
					mytypes1.appendChild(doc1.createTextNode(Integer
							.toString(types[i])));

					schemaRoot.appendChild(mytypes1);
					rootElement.appendChild(mytypes);
				}

				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				// StreamResult result = new StreamResult(new File(path + dbName
				// + "/"
				// + name + "/" + name));
				StreamResult result = new StreamResult(new File(path + dbName
						+ "\\" + name + "\\" + name));

				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
				DOMSource source1 = new DOMSource(doc1);
				// StreamResult result1 = new StreamResult(new File(path +
				// dbName
				// + "/" + "Schemas"));
				StreamResult result1 = new StreamResult(new File(path + dbName
						+ "\\" + "Schemas"));
				transformer.transform(source, result);
				transformer.transform(source1, result1);
				System.out.println("Table Created Successfully!");
			}
			else
			{
				System.out.println("Table Created Before!!");
			}
		}
		else
		{
			System.out.println("Database not selected");
		}
	}

	private static ArrayList<String> getFields(String name)
			throws ParserConfigurationException, SAXException, IOException
	{
		ArrayList<String> arr = new ArrayList<String>();
		// File fXmlFile = new File(path + dbName + "/Schemas");
		File fXmlFile = new File(path + dbName + "\\Schemas");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc2 = dBuilder.parse(fXmlFile);

		doc2.getDocumentElement().normalize();
		NodeList list1 = doc2.getElementsByTagName("schemas");
		NodeList list2 = list1.item(0).getChildNodes();
		for (int i = 0; i < list2.getLength(); i++)
		{
			if (name.equalsIgnoreCase(list2.item(i).getNodeName()))
			{
				NodeList nlist3 = list2.item(i).getChildNodes().item(0)
						.getChildNodes();
				for (int j = 0; j < nlist3.getLength(); j++)
				{
					arr.add(nlist3.item(j).getNodeName());
				}
				break;
			}
		}
		return arr;
	}

	public void insertIntoTable(String tablename, String[] attrs, String[] data)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerException
	{
		if (checkTable(tablename))
		{
			if (checkFields(tablename, attrs))
			{
				String name = tablename;
				// tablename = path + dbName + "/" + tablename + "/" +
				// tablename;
				tablename = path + dbName + "\\" + tablename + "\\" + tablename;
				if (validate(attrs, data, tablename))
				{
					ArrayList<String> arr = getFields(name);
					if (arr.size() >= attrs.length)
					{
						String[] arx = attrs;
						String[] datx = data;
						attrs = new String[arr.size()];
						data = new String[arr.size()];
						int i;
						for (i = 0; i < datx.length; i++)
						{
							attrs[i] = arx[i];
							data[i] = datx[i];
						}
						// System.out.println(i + " " + arr.size());
						for (int j = i; j < arr.size(); j++)
						{
							attrs[j] = arr.get(j);
							data[j] = Integer.toString(Integer.MAX_VALUE);
						}
					}
					// System.out.println(Arrays.toString(attrs));
					// System.out.println(Arrays.toString(data));
					DocumentBuilderFactory docFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder docBuilder = docFactory
							.newDocumentBuilder();
					Document doc = docBuilder.parse(new File(tablename));
					// root elements
					Element toadd = doc.createElement("Entry");
					for (int i = 0; i < data.length; i++)
					{
						Element attr = doc.createElement(attrs[i]);
						attr.appendChild(doc.createTextNode(data[i]));
						toadd.appendChild(attr);
					}
					doc.getDocumentElement().appendChild(toadd);
					TransformerFactory transformerFactory = TransformerFactory
							.newInstance();
					Transformer transformer = transformerFactory
							.newTransformer();
					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(new File(tablename));
					transformer.transform(source, result);
					System.out.println("Item inserted succesfully");
				}
				else
				{
					System.out.println("Error");
				}
			}
			else
			{
				System.out.println("Some fields are not found");
			}

		}
		else
		{
			System.out.println("Table not found");
		}
	}

	private static boolean validate(String[] attrs, String[] types,
			String tablename) throws ParserConfigurationException,
			SAXException, IOException
	{

		File fXmlFile = new File(tablename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);

		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("types");
		for (int i = 0; i < types.length; i++)
		{
			for (int j = 0; j < nList.getLength(); j++)
			{
				Node node = nList.item(j);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element temp = (Element) node;
					String see = temp
							.getElementsByTagName(attrs[i].toLowerCase())
							.item(0).getTextContent();
					if (see.equals(null))
						return false;
					else
					{
						switch (Integer.parseInt(see))
						{
							case 0:
								try
								{
									Integer.parseInt(types[i]);
								}
								catch (NumberFormatException e)
								{
									return false;
								}
								break;
							case 2:
								try
								{
									Double.parseDouble(types[i]);
								}
								catch (NumberFormatException e)
								{
									return false;
								}
								break;
						}
					}
				}
			}
		}
		return true;
	}

	private static boolean validate2(String tablename, String attrCondition,
			String compare) throws ParserConfigurationException, SAXException,
			IOException
	{
		File fXmlFile = new File(tablename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);

		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("types");
		NodeList nList1 = nList.item(0).getChildNodes();
		for (int i = 0; i < nList1.getLength(); i++)
		{
			Node n = nList1.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE)
			{
				Element temp = (Element) n;
				String see = temp.getTextContent();
				if (attrCondition.equals(temp.getNodeName()) && see.equals("1")
						&& !compare.equals("="))
					return false;
			}
		}
		return true;

	}

	public void delete(String tablename, String attrCondition,
			String dataCondition, String comparator2)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerException
	{
		if (checkTable(tablename))
		{
			String name = tablename;
			// tablename = path + dbName + "/" + tablename + "/" + tablename;
			tablename = path + dbName + "\\" + tablename + "\\" + tablename;
			String arr1[] = new String[1];
			arr1[0] = attrCondition;
			String arr2[] = new String[1];
			arr2[0] = dataCondition;
			if (checkFields(name, arr1))
			{
				if (validate(arr1, arr2, tablename))
				{
					if (validate2(tablename, attrCondition, comparator2))
					{
						DocumentBuilderFactory docFactory = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder docBuilder = docFactory
								.newDocumentBuilder();
						Document doc = docBuilder.parse(new File(tablename));
						// root elements
						NodeList list1 = doc.getElementsByTagName("Entry");
						for (int i = 0; i < list1.getLength(); i++)
						{
							NodeList list2 = list1.item(i).getChildNodes();
							for (int j = 0; j < list2.getLength(); j++)
							{
								String a = list2.item(j).getNodeName();
								if (a.equalsIgnoreCase(attrCondition))
								{
									String see = list2.item(j).getTextContent();
									switch (comparator2)
									{
										case "=":
											if (dataCondition.equals(see))
											{
												list1.item(i)
														.getParentNode()
														.removeChild(
																list1.item(i));
											}
											break;
										case ">":
											if (Double
													.parseDouble(dataCondition) < Double
													.parseDouble(see))
											{
												list1.item(i)
														.getParentNode()
														.removeChild(
																list1.item(i));
											}
											break;
										case "<":
											if (Double
													.parseDouble(dataCondition) > Double
													.parseDouble(see))
											{
												list1.item(i)
														.getParentNode()
														.removeChild(
																list1.item(i));
											}
											break;
									}
									break;
								}
							}
						}
						TransformerFactory transformerFactory = TransformerFactory
								.newInstance();
						Transformer transformer = transformerFactory
								.newTransformer();
						DOMSource source = new DOMSource(doc);
						StreamResult result = new StreamResult(new File(
								tablename));
						System.out.println("Entries Deleted");
						transformer.transform(source, result);
					}
					else
					{
						System.out.println("Error");
					}
				}
				else
				{
					System.out.println("Error");
				}
			}
			else
			{
				System.out.println("some fields are not found");
			}
		}
	}

	public void select(String tablename, String[] selected,
			String attrCondition, String dataCondition, String comparator2)
			throws ParserConfigurationException, SAXException, IOException
	{
		if (checkTable(tablename))
		{
			String[] arr = new String[1];
			arr[0] = attrCondition;
			if (checkFields(tablename, arr))
			{
				// tablename = path + dbName + "/" + tablename + "/" +
				// tablename;
				tablename = path + dbName + "\\" + tablename + "\\" + tablename;
				if (selected.length == 1 && selected[0] == "*")
				{
					File fXmlFile = new File(tablename);
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc2 = dBuilder.parse(fXmlFile);

					doc2.getDocumentElement().normalize();

					NodeList nList = doc2.getElementsByTagName("types");
					NodeList nList2 = nList.item(0).getChildNodes();
					selected = new String[nList2.getLength()];
					for (int j = 0; j < nList2.getLength(); j++)
					{

						Node node = nList2.item(j);
						String k = node.getNodeName();
						selected[j] = k;

					}

				}
				// ArrayList<String> container = new ArrayList<String>();
				String arr1[] = new String[1];
				arr1[0] = attrCondition;
				String arr2[] = new String[1];
				arr2[0] = dataCondition;
				if (validate(arr1, arr2, tablename))
				{
					if (validate2(tablename, attrCondition, comparator2))
					{
						DocumentBuilderFactory docFactory = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder docBuilder = docFactory
								.newDocumentBuilder();
						Document doc = docBuilder.parse(new File(tablename));
						// root elements
						NodeList list1 = doc.getElementsByTagName("Entry");
						for (int i = 0; i < list1.getLength(); i++)
						{
							NodeList list2 = list1.item(i).getChildNodes();
							for (int j = 0; j < list2.getLength(); j++)
							{
								String a = list2.item(j).getNodeName();
								if (a.equalsIgnoreCase(attrCondition))
								{
									String see = list2.item(j).getTextContent();
									switch (comparator2)
									{
										case "=":
											if (dataCondition.equals(see))
											{
												System.out
														.println("New Item found");
												for (int m = 0; m < list2
														.getLength(); m++)
												{
													String k = list2.item(m)
															.getNodeName();
													for (int c = 0; c < selected.length; c++)
													{
														if (k.equalsIgnoreCase(selected[c]))
														{
															String found = list2
																	.item(m)
																	.getTextContent();
															System.out
																	.println(selected[c]
																			+ " "
																			+ found);
														}
													}
												}

											}
											break;
										case ">":
											if (Double
													.parseDouble(dataCondition) < Double
													.parseDouble(see))
											{
												System.out
														.println("New Item found");
												for (int m = 0; m < list2
														.getLength(); m++)
												{

													String k = list2.item(m)
															.getNodeName();
													for (int c = 0; c < selected.length; c++)
													{
														if (k.equalsIgnoreCase(selected[c]))
														{
															String found = list2
																	.item(m)
																	.getTextContent();
															// container.add(found);
															System.out
																	.println(selected[c]
																			+ " "
																			+ found);
														}
													}
												}
											}
											break;
										case "<":
											if (Double
													.parseDouble(dataCondition) > Double
													.parseDouble(see))
											{
												System.out
														.println("New Item found");
												for (int m = 0; m < list2
														.getLength(); m++)
												{

													String k = list2.item(m)
															.getNodeName();
													for (int c = 0; c < selected.length; c++)
													{
														if (k.equalsIgnoreCase(selected[c]))
														{
															String found = list2
																	.item(m)
																	.getTextContent();
															// container.add(found);
															System.out
																	.println(selected[c]
																			+ " "
																			+ found);
														}
													}
												}
											}
											break;
									}
									break;
								}
							}
						}
					}
					else
					{
						System.out.println("Error");
					}
				}
				else
				{
					System.out.println("Error");
				}
			}
			else
			{
				System.out.println("Some fields are not found");
			}
		}
		else
		{
			System.out.println("Table not found");
		}
	}

	public void update(String tablename, String attrCondition,
			String dataCondition, String comparator2, String[] attrs,
			String[] data) throws ParserConfigurationException, SAXException,
			IOException, TransformerException
	{
		if (checkTable(tablename))
		{
			// tablename = path + dbName + "/" + tablename + "/" + tablename;
			tablename = path + dbName + "\\" + tablename + "\\" + tablename;
			String arr1[] = new String[1];
			arr1[0] = attrCondition;
			String arr2[] = new String[1];
			arr2[0] = dataCondition;
			if (validate(arr1, arr2, tablename)
					&& validate(attrs, data, tablename))
			{
				if (validate2(tablename, attrCondition, comparator2))
				{
					DocumentBuilderFactory docFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder docBuilder = docFactory
							.newDocumentBuilder();
					Document doc = docBuilder.parse(new File(tablename));
					// root elements
					NodeList list1 = doc.getElementsByTagName("Entry");
					for (int i = 0; i < list1.getLength(); i++)
					{
						NodeList list2 = list1.item(i).getChildNodes();
						for (int j = 0; j < list2.getLength(); j++)
						{
							String a = list2.item(j).getNodeName();
							if (a.equalsIgnoreCase(attrCondition))
							{
								String see = list2.item(j).getTextContent();
								switch (comparator2)
								{
									case "=":
										if (dataCondition.equals(see))
										{
											for (int m = 0; m < list2
													.getLength(); m++)
											{
												String k = list2.item(m)
														.getNodeName();
												for (int n = 0; n < attrs.length; n++)
												{
													if (attrs[n].equals(k))
													{
														list2.item(m)
																.setTextContent(
																		data[n]);
													}
												}
											}
										}
										break;
									case ">":
										if (Double.parseDouble(dataCondition) < Double
												.parseDouble(see))
										{
											for (int m = 0; m < list2
													.getLength(); m++)
											{
												String k = list2.item(m)
														.getNodeName();
												for (int n = 0; n < attrs.length; n++)
												{
													if (attrs[n].equals(k))
													{
														list2.item(m)
																.setTextContent(
																		data[n]);
													}
												}
											}
										}
										break;
									case "<":
										if (Double.parseDouble(dataCondition) > Double
												.parseDouble(see))
										{
											for (int m = 0; m < list2
													.getLength(); m++)
											{
												String k = list2.item(m)
														.getNodeName();
												for (int n = 0; n < attrs.length; n++)
												{
													if (attrs[n].equals(k))
													{
														list2.item(m)
																.setTextContent(
																		data[n]);
													}
												}
											}
										}
										break;
								}
								break;
							}
						}
					}
					TransformerFactory transformerFactory = TransformerFactory
							.newInstance();
					Transformer transformer = transformerFactory
							.newTransformer();
					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(new File(tablename));
					transformer.transform(source, result);
					System.out.println("Entry Updated");
				}
				else
				{
					System.out.println("Error in update");
				}
			}
			else
			{
				System.out.println("Error in update");
			}
		}
		else
		{
			System.out.println("Table not found");
		}
	}
}

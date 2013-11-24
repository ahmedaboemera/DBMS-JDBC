import java.io.IOException;
import java.util.HashMap;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class Parser
{
	private String all;
	private HashMap<String, Integer> type;
	private static SqlToXML sql;

	public Parser(String str) throws ParserConfigurationException,
			TransformerException, SAXException, IOException, JAXBException
	{
		all = str.toLowerCase();
		// System.out.println(all);
		type = new HashMap<String, Integer>();
		type.put("create", 1);
		type.put("update", 2);
		type.put("select", 3);
		type.put("delete", 4);
		type.put("use", 5);
		type.put("insert", 6);
		sql = new SqlToXML();
	}

	private static int valid_create(String s)
			throws ParserConfigurationException, TransformerException,
			JAXBException, IOException, SAXException
	{
		int ind = s.indexOf(" ");
		if (ind == -1)
		{
			return -1;
		}
		String t = s.substring(0, ind);
		String toSend = s.substring(ind + 1, s.length());
		if (t.compareTo("database") == 0)
		{
			return valid_createDatabase(toSend);
		}
		else if (t.compareTo("table") == 0)
		{
			return valid_createTable(toSend);
		}
		else
		{
			return -1;
		}
	}

	private static int valid_createDatabase(String s) throws ParserConfigurationException, TransformerException, IOException
	{
		int ind = s.indexOf(" ");
		if (ind == -1)
		{
			return -1;
		}
		String name = s.substring(0, ind);
		String rest = s.substring(ind + 1, s.length());
		if (rest.length() == 1 && validateName(name) == true)
		{
			sql.createDB(name);
			return 0;
		}
		else
		{
			return -1;
		}
	}

	private static int valid_createTable(String s)
			throws ParserConfigurationException, TransformerException,
			JAXBException, IOException, SAXException
	{
		int ind = s.indexOf(" ");
		if (ind == -1)
		{
			return -1;
		}
		String name = s.substring(0, ind);
		String rest = s.substring(ind + 1, s.length());
		if (validateName(name))
		{
			return valid_createTablePara(name, rest);
		}
		else
		{
			return -1;
		}
	}

	private static int valid_createTablePara(String tableName, String s)
			throws ParserConfigurationException, TransformerException,
			JAXBException, IOException, SAXException
	{
		if (s.length() < 3)
		{
			return -1;
		}
		if (s.charAt(0) == '(' && s.charAt(s.length() - 3) == ')')
		{
			String newString = s.substring(1, s.length() - 3);
			if (newString.length() == 0)
			{
				return -1;
			}
			String[] entry = newString.split(",");
			String[] name = new String[entry.length];
			int[] type = new int[entry.length];
			for (int i = 0; i < entry.length; i++)
			{
				String[] temp = entry[i].split(" ");
				if (temp.length == 2 && validateName(temp[0]) == true)
				{
					name[i] = temp[0];
					if (temp[1].compareTo("integer") == 0)
					{
						type[i] = 0;
					}
					else if (temp[1] != null
							&& temp[1].compareTo("string") == 0)
					{
						type[i] = 1;
					}
					else if (temp[1] != null
							&& temp[1].compareTo("double") == 0)
					{
						type[i] = 2;
					}
					else
					{
						return -1;
					}
				}
				else
				{
					return -1;
				}
			}
			// System.out.println("Create Table: " + tableName);
			// for (int i = 0; i < entry.length; i++)
			// {
			// System.out.println(name[i] + " " + type[i]);
			// }
			sql.createTable(tableName, name, type);
			return 1;
		}
		else
		{
			return -1;
		}
	}

	private static int valid_use(String s) throws IOException
	{
		int ind = s.indexOf(" ");
		if (ind == -1)
		{
			return -1;
		}
		String name = s.substring(0, ind);
		String rest = s.substring(ind + 1, s.length());
		if (rest.length() == 1 && validateName(name))
		{
			// System.out.println("Use Database: " + name);
			sql.useDB(name);
			return 5;
		}
		else
		{
			return -1;
		}
	}

	private static int validateDel_para(String t_n, String s)
			throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		int ind = s.indexOf(" ");
		if (ind == -1)
		{
			return -1;
		}
		String name = s.substring(0, ind);
		if (name.compareTo("where") == 0)
		{
			String rest = s.substring(ind + 1, s.length());
			String[] temp = rest.split(" ");
			if (temp.length == 4)
			{
				if (temp[1].compareTo("=") == 0 || temp[1].compareTo("<") == 0
						|| temp[1].compareTo(">") == 0)
				{
					if (validateName(temp[0]) == true && validateName(temp[2]))
					{
						sql.delete(t_n, temp[0], temp[2], temp[1]);
						// System.out.println("Delete from " + t_n + "where"
						// + temp[0] + temp[1] + temp[2]);
						return 4;
						// send your para
					}
					else
					{
						return -1;
					}
				}
				else
				{
					return -1;
				}
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return -1;
		}
	}

	private static int valid_delete(String s)
			throws ParserConfigurationException, SAXException, IOException, TransformerException
	{

		int ind = s.indexOf(" ");
		if (ind == -1)
		{
			return -1;
		}
		String name = s.substring(0, ind);
		if (name.compareTo("from") == 0)
		{
			String rest = s.substring(ind + 1, s.length());
			int ind2 = rest.indexOf(" ");
			if (ind2 == -1)
			{
				return -1;
			}
			String table_name = rest.substring(0, ind2);
			String rest_con = rest.substring(ind2 + 1, rest.length());
			if (validateName(table_name) == true)
			{
				return validateDel_para(table_name, rest_con);
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return -1;
		}
	}

	private static int select_final(String s, String[] atr)
			throws ParserConfigurationException, SAXException, IOException
	{
		int ind = s.indexOf(' ');
		if (ind == -1)
		{
			return -1;
		}
		String name = s.substring(0, ind);
		String rest1 = s.substring(ind + 1, s.length());
		if (validateName(name))
		{
			int ind2 = rest1.indexOf(' ');
			if (ind2 == -1)
			{
				return -1;
			}
			String where = rest1.substring(0, ind2);
			String rest2 = rest1.substring(ind2 + 1);
			if (where.compareTo("where") == 0)
			{
				String[] temp = rest2.split(" ");
				if (temp.length == 4)
				{
					if (temp[1].compareTo("=") == 0
							|| temp[1].compareTo("<") == 0
							|| temp[1].compareTo(">") == 0)
					{
						if (validateName(temp[0]) == true
								&& validateName(temp[2]))
						{
							// System.out.println("Select from " + name +
							// "where"
							// + temp[0] + temp[1] + temp[2]);
							sql.select(name, atr, temp[0], temp[2], temp[1]);
							// select all
							return 3;
						}
						else
						{
							return -1;
						}
					}
					else
					{
						return -1;
					}
				}
				else
				{
					return -1;
				}
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return -1;
		}
	}

	private static int select_wzCondition(String s)
			throws ParserConfigurationException, SAXException, IOException
	{
		int ind = s.indexOf(' ');
		if (ind == -1)
		{
			return -1;
		}
		String attributes = s.substring(0, ind);
		String rest = s.substring(ind + 1);
		String[] atr = attributes.split(",");
		for (int i = 0; i < atr.length; i++)
		{
			if (!validateName(atr[i]))
			{
				return -1;
			}
		}
		int ind2 = rest.indexOf(' ');
		if (ind2 == -1)
		{
			return -1;
		}
		String from = rest.substring(0, ind2);
		String rest2 = rest.substring(ind2 + 1);
		if (from.compareTo("from") == 0)
		{
			return select_final(rest2, atr);
		}
		else
		{
			return -1;
		}
	}

	private static int valid_select(String s)
			throws ParserConfigurationException, SAXException, IOException
	{
		int ind = s.indexOf(' ');
		if (ind == -1)
		{
			return -1;
		}
		String atr = s.substring(0, ind);
		String rest = s.substring(ind + 1, s.length());
		if (atr.compareTo("*") == 0)
		{
			String[] to_s = { "*" };
			int ind2 = rest.indexOf(' ');
			if (ind2 == -1)
			{
				return -1;
			}
			String from = rest.substring(0, ind2);
			String rest2 = rest.substring(ind2 + 1);
			if (from.compareTo("from") == 0)
			{
				return select_final(rest2, to_s);
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return select_wzCondition(s);
		}
	}

	private static int valid_insert_1(String s)
			throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		if (s.indexOf(' ') == -1)
		{
			return -1;
		}
		String name = s.substring(0, s.indexOf(' '));
		String rest = s.substring(s.indexOf(' ') + 1);
		if (validateName(name))
		{
			String[] temp = rest.split(" ");
			if (temp.length == 4)
			{
				if (temp[0].charAt(0) == '('
						&& temp[0].charAt(temp[0].length() - 1) == ')'
						&& temp[2].charAt(0) == '('
						&& temp[2].charAt(temp[2].length() - 1) == ')'
						&& temp[1].compareTo("values") == 0)
				{
					String fields = temp[0].substring(1, temp[0].length() - 1);
					String values = temp[2].substring(1, temp[2].length() - 1);
					if (fields.length() == 0 || values.length() == 0)
					{
						return -1;
					}
					String[] f = fields.split(",");
					String[] v = values.split(",");
					if (f.length == v.length)
					{
						// send the two arrays with the name
						// System.out.println(name);
						// System.out.println(fields + "\n" + values);
						sql.insertIntoTable(name, f, v);
						return 6;
					}
					else
					{
						return -1;
					}
				}
				else
				{
					return -1;
				}
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return -1;
		}
	}

	private static int valid_insert(String s)
			throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		if (s.indexOf(' ') == -1)
		{
			return -1;
		}
		String into = s.substring(0, s.indexOf(' '));
		String rest = s.substring(s.indexOf(' ') + 1);
		if (into.compareTo("into") == 0)
		{
			return valid_insert_1(rest);
		}
		else
		{
			return -1;
		}
	}

	private static int final_update(String t_b, String values,
			String condition) throws ParserConfigurationException,
			SAXException, IOException, TransformerException
	{
		// System.out.println(t_b+" ;; "+values+" ;; "+condition);
		String[] temp = values.split(",");
		String[] atrs = new String[temp.length];
		String[] vals = new String[temp.length];
		for (int i = 0; i < temp.length; i++)
		{
			String[] temp_d = temp[i].split(" ");
			if (temp_d.length != 3)
			{
				return -1;
			}
			if (temp_d[1].compareTo("=") != 0)
			{
				return -1;
			}
			vals[i] = temp_d[2];
			atrs[i] = temp_d[0];
		}
		String temp2[] = condition.split(" ");

		if (temp2.length != 4
				|| (temp2[1].compareTo("=") != 0
						&& temp2[1].compareTo("<") != 0 && temp2[1]
						.compareTo(">") != 0))
		{
			return -1;
		}

		// System.out.println("data sent :" + t_b + " " + temp2[0] + " "
		// + temp2[2]);
		sql.update(t_b, temp2[0], temp2[2], temp2[1], atrs, vals);
		return 2;
		// send atrs , vals , temp2[0] , temp2[2] , t_b;
	}

	private static int valid_update(String s)
			throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		if (s.indexOf(' ') == -1)
		{
			return -1;
		}
		String name = s.substring(0, s.indexOf(' '));
		String rest = s.substring(s.indexOf(' ') + 1);
		if (validateName(name))
		{
			if (rest.indexOf(' ') == -1)
			{
				return -1;
			}
			String set = rest.substring(0, rest.indexOf(' '));
			if (set.compareTo("set") != 0)
			{
				return -1;
			}
			if (rest.indexOf(' ') == -1)
			{
				return -1;
			}
			rest = rest.substring(rest.indexOf(' ') + 1);
			if (rest.indexOf("where") != -1)
			{
				if (rest.charAt(rest.indexOf("where") - 1) != ' ')
				{
					return -1;
				}
				String vals = rest.substring(0, rest.indexOf("where") - 1);
				String cond = rest.substring(rest.indexOf("where") + 6);
				// System.out.println(name);
				return final_update(name, vals, cond);
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return -1;
		}
	}

	public int findType() throws ParserConfigurationException,
			TransformerException, SAXException, IOException, JAXBException
	{
		int ind = all.indexOf(" ");
		if(ind == -1)
		{
			return -1;
		}
		String t = all.substring(0, ind);
		String toSend = all.substring(ind + 1, all.length());
		if (type.get(t) == null)
		{
			return -1;
		}
		else if (type.get(t) == 1)
		{
			return valid_create(toSend);
		}
		else if (type.get(t) == 2)
		{
			return valid_update(toSend);
		}
		else if (type.get(t) == 3)
		{
			return valid_select(toSend);
		}
		else if (type.get(t) == 4)
		{
			return valid_delete(toSend);
		}
		else if (type.get(t) == 5)
		{
			return valid_use(toSend);
		}
		else if (type.get(t) == 6)
		{
			return valid_insert(toSend);
		}
		return -1;
	}

	private static boolean validateName(String s)
	{
		for (int i = 0; i < s.length(); i++)
		{
			int val = s.charAt(i);
			if ((val >= 'a' && val <= 'z') || (val >= '0' && val <= '9')
					|| s.charAt(i) == '_' || s.charAt(i) == '\''
					|| s.charAt(i) == '@' || s.charAt(i) == '.'|| s.charAt(i) == '-')
			{
				continue;
			}
			else
			{
				return false;
			}
		}
		return true;
	}
}

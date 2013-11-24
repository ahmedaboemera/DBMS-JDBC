import java.io.IOException;
import java.util.Scanner;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class Main
{
	@SuppressWarnings("resource")
	public static void main(String[] args) throws ParserConfigurationException,
			TransformerException, SAXException, IOException, JAXBException
	{
		// for (String envName : env.keySet())
		// {
		// System.out.format("%s=%s%n", envName, env.get(envName));
		// }
		// System.out.println(".....");
//		 System.out.println(System.getenv("SHELL"));
		// SqlToXML.setPath(System.getenv("DBMS"));
		SqlToXML.setPath(System.getenv("DBMS"));
		Scanner in = new Scanner(System.in);
		String line_read;
		line_read = in.nextLine();
		while (line_read.length() == 0)
		{
			line_read = in.nextLine();
		}
		String input = "";
		while (line_read.charAt(0) != '0')
		{
			input += line_read;
			input += " ";
			input = edit(input);
			if (line_read.contains(";"))
			{
				int ind = line_read.indexOf(";");
				if (ind != line_read.length() - 1)
				{
					System.out.println("invalid command");
				}
				else
				{
					String fin = "";
					for (int i = 0; i < input.length() - 2; i++)
					{
						// System.out.println(i);
						if (input.charAt(i) == ' ')
						{
							if (i != input.length() - 1 && i != 0
									&& input.charAt(i + 1) != ','
									&& input.charAt(i - 1) != ',')
							{
								fin += input.charAt(i);
							}
						}
						else if (input.charAt(i) == '(')
						{
							if (i != 0 && input.charAt(i - 1) != ' ')
							{
								fin += ' ';
							}
							fin += input.charAt(i);
							if (i != input.length() - 1
									&& input.charAt(i + 1) == ' ')
							{
								i++;
							}
						}
						else if (input.charAt(i) == ')')
						{
							if (i != 0 && input.charAt(i - 1) == ' ')
							{
								fin = fin.substring(0, fin.length() - 1);
							}
							fin += input.charAt(i);
						}
						else if (input.charAt(i) == '='
								|| input.charAt(i) == '<'
								|| input.charAt(i) == '>')
						{
							if (i != 0 && input.charAt(i - 1) != ' ')
							{
								fin += ' ';
							}
							fin += input.charAt(i);
							if (i != input.length() - 1
									&& input.charAt(i + 1) != ' ')
							{
								fin += ' ';
							}
						}
						else if (input.charAt(i) == '*')
						{
							if (i != 0 && input.charAt(i - 1) != ' ')
							{
								fin += ' ';

							}
							fin += '*';
							if (i != input.length() - 1
									&& input.charAt(i + 1) != ' ')
							{
								fin += ' ';
							}

						}
						else
						{
							fin += input.charAt(i);
						}
					}
					// System.out.println(fin);
					if (input.charAt(input.length() - 3) != ' ')
					{
						fin += ' ';
					}
					// System.out.println(fin);
					fin += input.charAt(input.length() - 2);
					// System.out.println(fin);

					input = fin;
					// System.out.println(input);
					Parser p = new Parser(fin);
					if (p.findType() == -1)
					{
						System.out.println("invalid command");
					}
					input = "";
				}
			}
			line_read = in.nextLine();
			while (line_read.length() == 0)
			{
				line_read = in.nextLine();
			}
		}

	}

	public static String edit(String s)
	{
		boolean f_space = false;
		String to_ret = "";
		for (int i = 0; i < s.length(); i++)
		{
			if (s.charAt(i) != ' ')
			{
				f_space = false;
				to_ret += s.charAt(i);
			}
			else if (s.charAt(i) == ' ' && f_space == false)
			{
				to_ret += s.charAt(i);
				f_space = true;
			}

		}
		return to_ret;
	}

	public static String remove_coma(String inp)
	{
		int ind = inp.indexOf(',');

		if (inp.charAt(ind - 1) == ' ')
		{
			String p1 = inp.substring(0, ind - 1);
			String p2 = inp.substring(ind, inp.length());
			inp = p1 + p2;
		}
		if (inp.charAt(ind + 1) == ' ')
		{
			String p1 = inp.substring(0, ind + 1);
			String p2 = inp.substring(ind, inp.length());
			inp = p1 + p2;
		}
		return inp;
	}
}

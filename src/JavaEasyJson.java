import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.*;
import java.util.Vector;



public class JavaEasyJson {

	enum JsonNodeType
	{
		NODE_OBJECT,
		NODE_ARRAY,
	};
	 enum JsonValueType
	{
		VALUE_NUM_INT,
		VALUE_NUM_FLOAT,
		VALUE_STRING,
		VALUE_BOOL,
		VALUE_ARRAY,
		VALUE_OBJECT,
		VALUE_NULL,
	};
	
	static char JsonLeftBrace = '{';
	static char JsonRightBrace = '}';
	static char JsonLeftBracket = '[';
	static char JsonRightBracket = ']';
	static char JsonEscapeCharacter = '\\';
	static char JsonColon = ':';
	static char JsonDoubleQuote = '"';
	static char JsonComma = ',';
	static char JsonNodeRefrence = '.';
	static char JsonStar = '*';
	static char JsonHash = '#';
	static char JsonSlash = '/';
	

	
	public class JsonValue
	{
		JsonValue()
		{
			ok=0;
		}
		String ToString()
		{
			String temp=new String();
			if (type ==JavaEasyJson.JsonValueType.VALUE_STRING)
			{
				if(name!=null)
				{
					if (!name.isEmpty())
					{
						temp += "\"";
						temp += name;
						temp += "\"";
						temp += ":";
					}
				}
				temp += "\"";
				temp += str;
				temp += "\"";
			}
			else if (type == JavaEasyJson.JsonValueType.VALUE_OBJECT)
			{
				if(name!=null)
				{
					if (!name.isEmpty())
					{
						temp += "\"";
						temp += name;
						temp += "\"";
						temp += ":";
					}
				}
				
				temp += node.ToString();
			}
			else if (type == JavaEasyJson.JsonValueType.VALUE_ARRAY)
			{
				if(name!=null)
				{
					if (!name.isEmpty())
					{
						temp += "\"";
						temp += name;
						temp += "\"";
						temp += ":";
					}
				}
				temp += node.ToString();
			}
			else
			{	
				if(name!=null)
				{
					if (!name.isEmpty())
					{
						temp += "\"";
						temp += name;
						temp += "\"";
						temp += ":";
					}
				}
				temp += str;
			}
			return temp;		
		}
		JsonValueType type;
		String name;
		String str;
		int vi;
		double vd;
		boolean vbl;
		JsonNode  node;	
		int ok;
	};	

	public class JsonNode
	{
		JsonNode()
		{
			ok=0;
			values=new Vector<JsonValue>();
		}
		String ToString()
		{
			String temp=new String();
			if (type == JavaEasyJson.JsonNodeType.NODE_OBJECT)
			{
				temp += "{";
			}
			else
			{
				temp += "[";
			}
			for (int i = 0; i < values.size(); i++)
			{
				temp += values.elementAt(i).ToString();
				temp += ",";
			}
			temp = temp.substring(0, temp.length() - 1);
			if (type == JavaEasyJson.JsonNodeType.NODE_OBJECT)
			{
				temp += "}";
			}
			else
			{
				temp += "]";
			}
			return temp;			
		}
		Vector<JsonValue> values;
		JsonNodeType type;	
		int ok;
	};
	/*
	一个结构良好的JSON字符串或JSON文件可以解析为一个树形结构
	{}对象和[]数组可以认为是树上的一个节点
	"":"" 名称键值对可以认为是节点上的一个叶子
	JSON解析过程实际上就是构建这棵树的过程
	*/
	public class JsonLex
	{
		JsonNode ParseString(String jsonstring)
		{
			JsonNode root =new JsonNode();
			//json = AToU(jsonstring);
			json=jsonstring;
			currnetpos = 0;
			while (currnetpos<json.length())
			{
				if (json.charAt(currnetpos) == JsonLeftBrace)
				{
					currnetpos++;
					root = BulidJsonNode(null, JavaEasyJson.JsonNodeType.NODE_OBJECT);
					break;
				}
				else if(json.charAt(currnetpos)  == JsonLeftBracket)
				{
					currnetpos++;
					root = BulidJsonNode(null, JavaEasyJson.JsonNodeType.NODE_ARRAY);					
					break;
				}
				currnetpos++;
			}
			return root;
		}
		JsonValue BuildJsonValue(JsonNode parentnode)
		{
			boolean haskey=false;
			int JsonDoubleQuoteMeet = 0;
			JsonValue value=new JsonValue();
			String token = currenttoken;
			while (currnetpos<json.length())
			{
				if (TokenIsComment(token))
				{
					GoCommentEnd(token);
					token =GetNextToken(false);
				}
				else if (token.equals("{") )
				{
					value.node = BulidJsonNode(parentnode, JavaEasyJson.JsonNodeType.NODE_OBJECT);
					value.type = JavaEasyJson.JsonValueType.VALUE_OBJECT;
					value.node.type = JavaEasyJson.JsonNodeType.NODE_OBJECT;
					value.ok=1;
					return value;
				}
				else if (token.equals("["))
				{
					value.node = BulidJsonNode(parentnode, JavaEasyJson.JsonNodeType.NODE_ARRAY);
					value.node.type = JavaEasyJson.JsonNodeType.NODE_ARRAY;
					value.type = JavaEasyJson.JsonValueType.VALUE_ARRAY;
					value.ok=1;
					return value;
				}
				else if (token.equals("}"))
				{
					//返回，交给上层处理
					currnetpos--;
					break;
				}
				else if (token.equals("]") )
				{
					//返回，交给上层处理
					currnetpos--;
					break;
				}
				else if (token.equals("\"") )
				{
					JsonDoubleQuoteMeet++;
					if (value.name!=null)
						value.type = JavaEasyJson.JsonValueType.VALUE_STRING;		
					if(JsonDoubleQuoteMeet%2==0)
						token = GetNextToken(false);
					else
						token = GetNextToken(true);
				}
				else if (token .equals(":"))
				{
					if (JsonDoubleQuoteMeet == 2)
					{
						if (value.name==null)
						{
							haskey = false;
						}
						else
						{
							haskey = true;
						}
					}
					else if(JsonDoubleQuoteMeet==3)
					{
						haskey = true;
					}		
					else
					{
						haskey = false;
					}
					value.type = JavaEasyJson.JsonValueType.VALUE_NULL;
					token =GetNextToken(false);
					if (currnetpos== json.length())
					{
						currnetpos--;
					}
				}
				else if (token.equals(",") )
				{
					break;		
				}
				else
				{
					if (value.name==null)
					{						
						value.name = token;						
					}		
					else if (value.str==null)
					{				
						AssignStringToJsonValue(value, token);
						if (json.charAt(currnetpos) == '"')
						{
							currnetpos++;
						}
						break;
					}
					token =GetNextToken(false);
				}		
			}
			if (!haskey)
			{
				AssignStringToJsonValue(value, value.name);
				value.name = "";	
				value.ok=1;
			}
			return value;
		}
		JsonNode BulidJsonNode(JsonNode parentnode, JsonNodeType nodetype)
		{
			JsonNode node= new JsonNode();
			node.type = nodetype;
			String token =GetNextToken(false);	
			while (currnetpos != json.length())
			{
				if (TokenIsComment(token))
				{
					GoCommentEnd(token);
					token =GetNextToken(false);
				}
				else if (token.equals("\"") )
				{
					node.values.addElement(BuildJsonValue(node));
					token =GetNextToken(false);
				}
				else if (token.equals("}"))
				{
					if (nodetype == JavaEasyJson.JsonNodeType.NODE_ARRAY)
					{
						token =GetNextToken(false);
					}
					else
					{
						break;
					}				
				}
				else if (token.equals("{"))
				{
					node.values.addElement(BuildJsonValue(node));
					token =GetNextToken(false);
				}
				else if (token.equals( "["))
				{
					node.values.addElement(BuildJsonValue(node));
					token =GetNextToken(false);
				}
				else if (token.equals("]"))
				{
					break;
				}
				else if (token.equals( ":"))
				{
					token =GetNextToken(false);
				}
				else if (token.equals(","))
				{
					token =GetNextToken(false);
				}
				else
				{
					node.values.addElement(BuildJsonValue(node));
					token =GetNextToken(false);
				}
			}		
			node.ok = 1;
			return node;
		}
		void GoCommentEnd(String commentstyle)
		{
			if (commentstyle.equals("//"))                            //cpp style
			{
				while (currnetpos < json.length())
				{
					if (json.charAt(currnetpos) == '\n')							 //unix style
					{
						currnetpos++;
						break;
					}
					else if (json.charAt(currnetpos)  == '\r' && json.charAt(currnetpos+1) =='\n')       //windows style
					{
						currnetpos++; currnetpos++;
						break;
					}
					currnetpos++;
				}
			}
			else if (commentstyle.equals("/*"))                      //c style
			{
				while (currnetpos < json.length())
				{
					if (json.charAt(currnetpos)  == '*' && json.charAt(currnetpos+1) == '/')
					{
						currnetpos++; currnetpos++;
						break;
					}
					currnetpos++;
				}
			}
		}
		boolean TokenIsComment(String token)
		{
			boolean bret = false;
			if (token.equals("//" )|| token.equals( "/*" )|| token.equals("*/") )
			{
				bret = true;
			}	
			return bret;
		}
		String GetNextToken(boolean tonextJsonDoubleQuote)
		{
			prevtoken = currenttoken;
			String token = new String();			
			if(tonextJsonDoubleQuote)
			{
				if (json.charAt(currnetpos)== JsonDoubleQuote)
					currnetpos++;
				while (currnetpos < json.length())
				{
					if (json.charAt(currnetpos)== JsonEscapeCharacter)
					{
						if (currnetpos+1 != json.length())
						{
							if (json.charAt(currnetpos+1) == JsonDoubleQuote)
							{
								token += JsonDoubleQuote;
								currnetpos++;
								currnetpos++;
							}
							else
							{
								token += json.charAt(currnetpos);
								currnetpos++;
							}
						}
					}
					else
					{
						if (json.charAt(currnetpos)==JsonDoubleQuote )
						{
							break;
						}				
						else
						{
							token += json.charAt(currnetpos);
							currnetpos++;
						}				
					}
					
				}
				currenttoken = token;
				return token;
			}
			else
			{
				while (currnetpos < json.length())
				{
					if (json.charAt(currnetpos) == JsonDoubleQuote ||
						json.charAt(currnetpos) == JsonRightBrace ||
						json.charAt(currnetpos) == JsonLeftBrace ||
						json.charAt(currnetpos)== JsonLeftBracket ||
						json.charAt(currnetpos) == JsonRightBracket ||
						json.charAt(currnetpos) == JsonColon ||
						json.charAt(currnetpos) == JsonComma)
					{
						if (token.isEmpty())
						{
							token="";
							token += json.charAt(currnetpos);
							currnetpos++;
							break;
						}
						else
						{

							break;
						}
					}
					else if (json.charAt(currnetpos) == JsonSlash)
					{
						//possible is comment
						if (((currnetpos + 1) != json.length()) && (json.charAt(currnetpos+1) == JsonSlash))
						{
							token = "//";
							currnetpos++; currnetpos++;
							break;
						}
						else if (((currnetpos + 1) != json.length()) && (json.charAt(currnetpos+1) == JsonStar))
						{
							token = "/*";
							currnetpos++; currnetpos++;
							break;
						}
						else
						{
							token += json.charAt(currnetpos);
						}
					}
					else if (json.charAt(currnetpos) == JsonEscapeCharacter)
					{
						if (!token.isEmpty())
						{
							if (json.charAt(currnetpos+1) == JsonDoubleQuote)
							{
								token += JsonDoubleQuote;
								currnetpos++;
							}
							else if (json.charAt(currnetpos+1) == 'b')
							{
								token += '\b';
								currnetpos++;
							}
							else if (json.charAt(currnetpos+1) == 'f')
							{
								token += '\f';
								currnetpos++;
							}
							else if (json.charAt(currnetpos+1) == 't')
							{
								token += '\t';
								currnetpos++;
							}
							else if (json.charAt(currnetpos+1) == 'n')
							{
								token += '\n';
								currnetpos++;
							}
							else if (json.charAt(currnetpos+1) == 'r')
							{
								token += '\r';
								currnetpos++;
							}
							else if (json.charAt(currnetpos+1)== '\\')
							{
								token += '\\';
								currnetpos++;
							}
							else if (json.charAt(currnetpos+1) == '/')
							{
								token += '/';
								currnetpos++;
							}
							else if (json.charAt(currnetpos+1) == 'u')
							{
								token +=json.charAt(currnetpos);
							}
							else
							{
								token += json.charAt(currnetpos);
							}
						}
					}
					else  if (json.charAt(currnetpos)== ' ' ||
							json.charAt(currnetpos) == '\t' ||
							json.charAt(currnetpos)== '\n' ||
							json.charAt(currnetpos)== '\r'
						)
					{
						;
					}
					else
					{
						token +=json.charAt(currnetpos);
					}
					currnetpos++;
				}
			}
			
			currenttoken = token;
			return token;
		}
		boolean AssignStringToJsonValue(JsonValue value, String text)
		{
			value.str = text;
			if (value.type == JavaEasyJson.JsonValueType.VALUE_STRING)
			{
				value.str = text;
			}
			else
			{
				String strJsonNodeRefrence=new String();
				strJsonNodeRefrence+=JsonNodeRefrence;
				
				if (text.equals("null"))
				{
					value.type = JavaEasyJson.JsonValueType.VALUE_NULL;
				}
				else if (text.equals("true"))
				{
					value.type = JavaEasyJson.JsonValueType.VALUE_BOOL;
					value.vbl = true;
				}
				else if (text.equals("false"))
				{
					value.type = JavaEasyJson.JsonValueType.VALUE_BOOL;
					value.vbl = false;
				}
				else if (text.contains((strJsonNodeRefrence)))
				{					
					value.type = JavaEasyJson.JsonValueType.VALUE_NUM_FLOAT;
					value.vd =  Double.parseDouble(text); 
				}
				else
				{
					value.type = JavaEasyJson.JsonValueType.VALUE_NUM_INT;
					value.vi =Integer.parseInt(text); 
				}	
			}
			return false;
		}
		String json;
		String currenttoken;
		String prevtoken;
		int currnetpos;
	};

		boolean ParseString(String jsonstring)
		{
			jsonlex = new JsonLex();
			jsonroot= new JsonNode();
			jsoncontent = new String();
			jsonroot =  jsonlex.ParseString(jsonstring);
			return true;
		}
		boolean ParseFile(String jsonfile)
		{			
	         File file=new File(jsonfile);
	         if(file.exists())
	         {	        	 
		         BufferedReader br=new BufferedReader(new FileReader(file));
		         String temp=null;
		         StringBuffer sb=new StringBuffer();
		         temp=br.readLine();
		         while(temp!=null)
		         {
		             sb.append(temp+" ");
		             temp=br.readLine();
		         }
		         jsonroot =  jsonlex.ParseString(sb.toString());	  
		         br.close();
	         }
	         return true;
		}

		//路径方式
		//节点名称.子节点名称.数组节点名称[数组元素下表].值名称
		boolean  GetValue(String nodepath,String value)
		{
			boolean bret = false;
			JsonValue val=new JsonValue();
			if(GetValue(nodepath,val))
			{
				if (val.type == JavaEasyJson.JsonValueType.VALUE_STRING)
				{
					value =  val.str;
					bret =true;
				}
			}			
			return bret;
		}
		boolean SetValue(String nodepath, String value)
		{
			boolean bret = false;
			JsonValue val=new JsonValue();
			val.type = JavaEasyJson.JsonValueType.VALUE_STRING;
			val.str = value;
			bret = SetValue(nodepath,val);	
			return bret;
		}
		int GetValue(String nodepath)
		{
			int value=-1;
			JsonValue val=new JsonValue();
			if (GetValue(nodepath,val))
			{
				if (val.type == JavaEasyJson.JsonValueType.VALUE_NUM_INT)
				{
					value = val.vi;
				}
			}
			return value;
		}
		boolean SetValue(String nodepath, int  value)
		{
			boolean bret = false;
			JsonValue val = new JsonValue();
			val.type = JavaEasyJson.JsonValueType.VALUE_NUM_INT;		
			val.vi =value ;
			val.str = Integer.toString(value);
			bret = SetValue(nodepath, val);	
			return bret;
		}		
		boolean SetValue(String nodepath, double  value)
		{
			boolean bret = false;
			JsonValue val = new JsonValue();
			val.type = JavaEasyJson.JsonValueType.VALUE_NUM_FLOAT;
			val.vd = value;			
			val.str = Double.toString(value);
			bret = SetValue(nodepath, val);
			return bret;
		}
		boolean SetValue(String nodepath, boolean  value)
		{
			boolean bret = false;
			JsonValue val = new JsonValue();
			val.type = JavaEasyJson.JsonValueType.VALUE_BOOL;
			val.vbl = value;
			if (val.vbl)
			{
				val.str = "true";
			}
			else
			{
				val.str = "false";
			}
			bret = SetValue(nodepath, val);
			return bret;
		}
		boolean SetNullValue(String nodepath)
		{
			boolean bret = false;
			JsonValue val = new JsonValue();
			val.type = JavaEasyJson.JsonValueType.VALUE_NULL;
			val.str = "null";
			bret = SetValue(nodepath, val);
			return bret;
		}
		boolean  GetValue(String nodepath,JsonValue jsvalue)
		{		
			boolean bret = false;;
			int index = -1;
			String keyname="";
			JsonNode node = FindNodeInternal(nodepath, jsonroot, index, keyname);
			if (node.ok==1)
			{
				int valuecount = node.values.size();
				int pos1 = keyname.indexOf(JsonLeftBracket);
				int pos2 = keyname.indexOf(JsonRightBracket);
				if (pos1 != -1 && pos2 == keyname.length() - 1)
				{
					int valueindex = Integer.parseInt(keyname.substring(pos1 + 1, pos2 - pos1 - 1));
					if (valueindex >= 0 && valueindex < valuecount)
					{						
						jsvalue = node.values.get(valueindex);
						bret = true;
					}
				}
				else
				{
					for (int i = 0; i < valuecount; i++)
					{				
						if (node.values.get(i).name == keyname)
						{
							jsvalue = node.values.get(i);
							bret = true;
							break;					
						}
					}
				}
			}
			return bret;
		}
		boolean SetValue(String nodepath, JsonValue newjsvalue)
		{
			boolean bret = false;
			if (newjsvalue.ok==1)
			{
				JsonValue jsvalue = new JsonValue();
				int index = -1;
				String keyname=new String();
				JsonNode node = FindNodeInternal(nodepath, jsonroot, index, keyname);
				if (node.ok==1)
				{
					int valuecount = node.values.size();
					int pos1 = keyname.indexOf(JsonLeftBracket);
					int pos2 = keyname.indexOf(JsonRightBracket);
					if (pos1 != -1 && pos2 == keyname.length() - 1)
					{
						int valueindex = Integer.parseInt(keyname.substring(pos1 + 1, pos2 - pos1 - 1));
						if (valueindex >= 0 && valueindex < valuecount)
						{
							bret = true;
							jsvalue = node.values.get(valueindex);
							newjsvalue.name = jsvalue.name;	
							node.values.removeElement(jsvalue);
							node.values.insertElementAt(newjsvalue, valueindex);
						}
					}
					else
					{
						for (int i = 0; i < valuecount; i++)
						{
							if (node.values.get(i).name == keyname)
							{
								jsvalue = node.values.get(i);
								newjsvalue.name = jsvalue.name;							
								node.values.removeElement(jsvalue);
								node.values.insertElementAt(newjsvalue, i);
								bret = true;
								break;
							}
						}
					}
				}
			}	
			return bret;
		}
		boolean DelValue(String nodepath)
		{
			boolean bret = false;			
			int index = -1;
			String keyname=new String();
			JsonNode node = FindNodeInternal(nodepath, jsonroot, index, keyname);;
			if (node.ok==1)
			{
				int valuecount = node.values.size();
				int pos1 = keyname.indexOf(JsonLeftBracket);
				int pos2 = keyname.indexOf(JsonRightBracket);
				if (pos1 != -1 && pos2 == keyname.length() - 1)
				{
					int valueindex = Integer.parseInt(keyname.substring(pos1 + 1, pos2 - pos1 - 1));
					if (valueindex >= 0 && valueindex < valuecount)
					{
						bret = true;
						node.values.remove(valueindex);
					}
				}
				else
				{
					for (int i = 0; i < valuecount; i++)
					{
						if (node.values.get(i).name == keyname)
						{
							bret = true;
							node.values.remove(i);
							break;
						}
					}
				}
			}	
			return bret;
		}

		//按节点逐层访问方式
		boolean AppendValue(JsonNode  node, String name, String value)
		{
			boolean bret = false;
			if (node.ok==1)
			{
				JsonValue val = new JsonValue();			
				val.type = JavaEasyJson.JsonValueType.VALUE_STRING;
				val.name = name;
				val.str = value;
				val.ok= 1;
				node.values.addElement(val);
				}
			return bret;
		}
		boolean AppendValue(JsonNode  node, String name, int value)
		{
			boolean bret = false;
			if (node.ok==1)
			{
				JsonValue val = new JsonValue();			
				val.type = JavaEasyJson.JsonValueType.VALUE_NUM_INT;
				val.name = name;
				val.str = Integer.toString(value);
				val.vi=value;
				val.ok= 1;
				node.values.addElement(val);
				}
			return bret;
		}
		boolean AppendValue(JsonNode  node, String name, double value)
		{
			boolean bret = false;
			if (node.ok==1)
			{
				JsonValue val = new JsonValue();			
				val.type = JavaEasyJson.JsonValueType.VALUE_NUM_FLOAT;
				val.name = name;
				val.str = Double.toString(value);
				val.vd=value;
				val.ok= 1;
				node.values.addElement(val);
				}
			return bret;
		}
		boolean AppendValue(JsonNode  node, String name, boolean value)
		{
			boolean bret = false;
			if (node.ok==1)
			{
				JsonValue val = new JsonValue();			
				val.type = JavaEasyJson.JsonValueType.VALUE_BOOL;
				val.name = name;
				if(value)
					val.str ="true";
				else
					val.str="false";
				val.vbl=value;
				val.ok= 1;
				node.values.addElement(val);
				}
			return bret;
		}
		boolean AppendNullValue(JsonNode  node, String name)
		{
			boolean bret = false;
			if (node.ok==1)
			{
				JsonValue val = new JsonValue();			
				val.type = JavaEasyJson.JsonValueType.VALUE_NULL;
				val.name = name;
				val.str ="null";				
				val.ok= 1;
				node.values.addElement(val);
				}
			return bret;
		}
		boolean AppendObjectValue(JsonNode  node, String name, JsonNode obj)
		{
			boolean bret = false;
			if (node.ok==1)
			{
				JsonValue  val = new JsonValue();				
				val.type = JavaEasyJson.JsonValueType.VALUE_OBJECT;
				val.name = name;
				val.node = obj;
				val.ok= 1;
				node.values.addElement(val);				
			}
			return bret;
		}
		boolean AppendArrayValue(JsonNode  node, String name, JsonNode objarray)
		{
			boolean bret = false;
			if (node.ok==1)
			{
				JsonValue  val = new JsonValue();				
				val.type = JavaEasyJson.JsonValueType.VALUE_ARRAY;
				val.name = name;
				val.node = objarray;
				val.ok= 1;
				node.values.addElement(val);				
			}
			return bret;
		}
		JsonValue   GetValue(JsonNode  node, String name)
		{
			JsonValue  val = new JsonValue();
			if (node.ok==1 && !name.isEmpty())
			{
				for (int i = 0; i < node.values.size(); i++)
				{
					
					if(node.values.get(i).name==name)
					{
						val = node.values.get(i) ;
						break;
					}
				}
			}
			return val;
		}
		JsonValue   GetValue(JsonNode  node, int index)
		{
			JsonValue val = new JsonValue();
			if (node.ok==1 )
			{
				if (index >= 0 && index < node.values.size())
				{
					val = node.values.get(index);			
				}
			}
			return val;
		}
		boolean DelValue(JsonNode  node, String name)
		{
			boolean bret = false;
			if (node.ok==1 && !name.isEmpty())
			{
				for (int i = 0; i < node.values.size(); i++)
				{
					JsonValue  val = node.values.get(i);
					if (val.ok==1)
					{
						if (val.name == name)
						{
							node.values.removeElementAt(i);
							bret = true;
							break;
						}
					}
				}
			}
			return bret;
		}
		boolean DelValue(JsonNode  node, int index)
		{
			boolean bret = false;
			if (node.ok==1)
			{
				if (index >= 0 && index < node.values.size())
				{
					node.values.removeElementAt(index);
					bret = true;					
				}
			}
			return bret;
		}

		JsonNode GetRoot()
		{
			return jsonroot;
		}
		boolean SetRoot(JsonNode node)
		{
			jsonroot=node;
			return true;
		}
		String ToString()
		{
			String jsonstra=new String();
			if (jsonroot.ok==1)
			{
				jsonstra = jsonroot.ToString();
				jsonstra = WellFormat(jsonstra);
			}
			return jsonstra;
		}
		boolean SaveToFile(String jsonfile)
		{
			boolean bret =false;
			  try {   
				FileOutputStream out = null;   
				out = new FileOutputStream(new File(jsonfile));   
				out.write(ToString().getBytes());   
				out.close();   
				bret =true;
			  }catch(Exception e)
			  {
				  
			  }
			return bret;
		}
		String WellFormat(String jsoncontent)
		{
			String jsonstra = jsoncontent;
			jsoncontent = "";
			int it = 0;
			int count = 0;
			int tablecount = 0;
			while (it != jsonstra.length())
			{
				if (jsonstra.charAt(it) == '"')
				{
					count++;
				}
				if ((count & 0x01) == 0x01)
				{
					jsoncontent += jsonstra.charAt(it);
				}
				else
				{
					if (jsonstra.charAt(it) == '{')
					{
						tablecount++;
						jsoncontent += "\r\n";
						for (int i = 0; i < tablecount; i++)
						{
							jsoncontent += "\t";
						}
						jsoncontent += jsonstra.charAt(it);
						jsoncontent += "\r\n";
						for (int i = 0; i < tablecount; i++)
						{
							jsoncontent += "\t";
						}
					}
					else if (jsonstra.charAt(it) == '[')
					{
						tablecount++;
						jsoncontent += "\r\n";
						for (int i = 0; i < tablecount; i++)
						{
							jsoncontent += "\t";
						}
						jsoncontent += jsonstra.charAt(it);
						for (int i = 0; i < tablecount; i++)
						{
							jsoncontent += "\t";
						}
					}
					else if (jsonstra.charAt(it) == ',')
					{
						jsoncontent += jsonstra.charAt(it);
						jsoncontent += "\r\n";
						for (int i = 0; i < tablecount; i++)
						{
							jsoncontent += "\t";
						}

					}
					else if (jsonstra.charAt(it) == '}' || jsonstra.charAt(it) == ']')
					{
						jsoncontent += "\r\n";
						for (int i = 0; i < tablecount; i++)
						{
							jsoncontent += "\t";
						}
						jsoncontent += jsonstra.charAt(it);
						tablecount--;
					}
					else
					{
						jsoncontent += jsonstra.charAt(it);
					}
				}
				it++;
			}	
			return jsoncontent;
		}

		JsonNode FindNodeInternal(String path, JsonNode parentnode,int index, String keyname)
		{
			JsonNode node =new JsonNode();
			if(parentnode.ok==0)
			{
				return node;
			}
			String sub = path;
			if (path.isEmpty())
			{
				return node;
			}
			int pos = path.indexOf(JsonNodeRefrence);
			if (pos != -1)
			{
				path = path.substring(0, pos);
				JsonNode inode=new JsonNode() ;
				inode = FindNodeInternal(path, parentnode,index,keyname);
				sub = sub.substring(pos + 1, sub.length() - pos - 1);
				if(inode.ok==1)
				node = FindNodeInternal(sub, inode,index, keyname);
			}
			else
			{
				int pos1 = sub.indexOf(JsonLeftBracket);
				int pos2 = sub.indexOf(JsonRightBracket);
				if (pos1 != -1 && pos2 == sub.length() - 1)
				{
					JsonNode inode=new JsonNode() ;
					if (pos1 != 0)
					{
						path = path.substring(0, pos1);
						
						inode = FindNodeInternal(path, parentnode, index, keyname);
					}
					else
					{
						inode = parentnode;
					}
					
					if(inode.ok==1)
					//if (parentnode->type == NODE_OBJECT)
					{
						keyname = sub;
						index =  Integer.parseInt(sub.substring(pos1+1, pos2 - pos1 - 1));
						JsonNode  inode2 =new JsonNode() ;
						if (index>=0 && index<inode.values.size())
							inode2= inode.values.get(index).node;
						if (inode2.ok == 0)
						{					
							return inode;
						}
						else
						{
							return inode2;
						}
					}
					
				}
				else
				{
					keyname = path;
					if (parentnode.type == JavaEasyJson.JsonNodeType.NODE_OBJECT)
					{
						int count = parentnode.values.size();
						for (int i = 0; i < count; i++)
						{
							if (parentnode.values.get(i).name == path)
							{
								if (parentnode.values.get(i).node.ok==0)
								{
									node = parentnode.values.get(i).node;
								}
								else
								{
									node = parentnode;
								}						
								break;
							}
						}
						
					}
					else if (parentnode.type == JavaEasyJson.JsonNodeType.NODE_ARRAY)
					{
						if (index >= 0 && index <parentnode.values.size())
						{
							if (parentnode.values.get(index).node.ok==1)
								return parentnode.values.get(index).node;
							else
								return parentnode;
						}
					}
				}		
			}	
			return node;
		}
		String jsoncontent;
		JsonLex jsonlex;
		JsonNode jsonroot;
	

}

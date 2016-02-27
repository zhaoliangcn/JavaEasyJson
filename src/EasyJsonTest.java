import java.util.*;

public class EasyJsonTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String json="{}";
		System.out.print(json);
		
		JavaEasyJson ejson= new JavaEasyJson();
		ejson.ParseString("{\"abc\":1234}");	
		ejson.ParseString("{\"chinese\":\"ÖÐÎÄ\"}");
		JavaEasyJson.JsonNode jsonnode =  ejson.new JsonNode();
		jsonnode=ejson.GetRoot();
		ejson.AppendValue(jsonnode, "english", "jsonstr");
		
		System.out.print(ejson.ToString());
		
		ejson.ParseString("{/*this is test comment*/\"firstName\":\"Bret\\b\\t\\\"t\\u9001\"}");
		System.out.print(ejson.ToString());
		
		ejson.ParseString("{\"people\":[{\"firstName\":\"Brett\",\"lastName\":\"McLaughlin\",\"email\":\"aaaa\"},{\"firstName\":\"Jason\",\"lastName\":\"Hunter\",\"email\":\"bbbb\"},{\"firstName\":\"Elliotte\",\"lastName\":\"Harold\",\"email\":\"cccc\"}]}");
		System.out.print(ejson.ToString());
		
		ejson.ParseString("{\"key1\": true,\"key2\":false,\"key3\":333,\"key4\":3.14,\"key5\":\"key5\"}");
		System.out.print(ejson.ToString());
		
	}
}

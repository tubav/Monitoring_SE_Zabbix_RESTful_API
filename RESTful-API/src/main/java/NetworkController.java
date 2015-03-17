/* 
************************************************************************

# RESTful API for Zabbix based on Java.
#
# © Copyright 2014 Yahya Al-Hazmi, TU Berlin.
# Authors: Mingyuan Wu and Yahya Al-Hazmi
# Licensed under the Apache License, Version 2.0 (the "License"); 
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0 
#
# Unless required by applicable law or agreed to in writing, software 
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License
#
# This API allows you to request monitoring information from Zabbix-Server 
# through Zabbix-API

 ************************************************************************

 This class is aimed to achieve the goal of getting network information 
 from zabbix.
 
 ************************************************************************
*/


import java.util.concurrent.atomic.AtomicLong;
import java.util.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.Properties;
import java.io.FileInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PutMethod;

@Controller
public class NetworkController{

	private final AtomicLong counter = new AtomicLong();
	private static String LOOUT = 	"Outgoing traffic on interface lo";
	private static String LOIN =	"Incoming traffic on interface lo";

	private static String ETH1OUT = "Outgoing traffic on interface eth1";
	private static String ETH0OUT = "Outgoing traffic on interface eth0";

	private static String ETH1IN = 	"Incoming traffic on interface eth1";
	private static String ETH0IN = 	"Incoming traffic on interface eth0";
	private static String LATENCY = "Network latency";
	private static String PACKETLOSS = "Percentage of packet loss";

	public String setRequest(String key_, String authentication, String hostid){
		// create json object for apiinfo.version 
		JSONArray 	list 	= new JSONArray();
		JSONObject 	jsonObj = new JSONObject();
		JSONObject 	search 	= new JSONObject();
		JSONObject 	params 	= new JSONObject();

		jsonObj.put("jsonrpc","2.0");
		jsonObj.put("method","item.get");

		params.put("output","extend");
		params.put("hostid",hostid);
		params.put("search", search);
		params.put("sortfield","name");	

		search.put("key_",key_);

		jsonObj.put("params",params);
		jsonObj.put("auth", authentication);// todo
		jsonObj.put("id",new Integer(1));
	
		return jsonObj.toString();
 	   }	

	public boolean comparision(String A, String B, JSONObject C, String D){
		return (A.equals(B) && C.get("name").equals(D));
	}

	public Network getNetwork(JSONObject obj, String hostid, String type){
		String network="";
		String clock="";
		String metricType="";

 	  	network = (String) obj.get("lastvalue");
 		clock = (String) obj.get("lastclock");	
		metricType = type;

		return new Network(hostid, type, network, clock);	
	}	
	

	public Network networkResponse(String hostid, PutMethod putMethod, String name){	
		JSONParser parser=new JSONParser();

		String loginResponse = "";
		HttpClient client = new HttpClient();

		try {
			client.executeMethod(putMethod); // send to request to the zabbix api
			loginResponse = putMethod.getResponseBodyAsString(); // read the result of the response
 	       		Object obj = parser.parse(loginResponse);
 		       	JSONObject obj2 = (JSONObject) obj;
        		String jsonrpc= (String) obj2.get("jsonrpc");

        		JSONArray array = (JSONArray) obj2.get("result");
			
	    		for (int i = 0; i < array.size(); i++){
   	        		JSONObject tobj = (JSONObject) array.get(i);
		   		if (!tobj.get("hostid").equals(hostid)) 		continue;
				if (comparision(name,"loOut",tobj,LOOUT)) 		return getNetwork(tobj, hostid, LOOUT); 
				else if (comparision(name,"loIn",tobj,LOIN)) 		return getNetwork(tobj, hostid, LOIN); 
				else if (comparision(name,"eth1Out",tobj,ETH1OUT)) 	return getNetwork(tobj, hostid, ETH1OUT); 
				else if (comparision(name,"eth0Out",tobj,ETH0OUT)) 	return getNetwork(tobj, hostid, ETH0OUT); 
				else if (comparision(name,"eth0In",tobj,ETH0IN)) 	return getNetwork(tobj, hostid, ETH0IN); 
				else if (comparision(name,"eth1In",tobj,ETH1IN)) 	return getNetwork(tobj, hostid, ETH1IN); 
				else if (comparision(name,"latency",tobj,LATENCY)) 	return getNetwork(tobj, hostid, LATENCY);
				else if (comparision(name,"packetloss",tobj,PACKETLOSS)) return getNetwork(tobj, hostid, PACKETLOSS); 
				else continue;
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(ParseException pe){
	        	System.out.println("Error");
		}

		return new Network("Error: please provide the appropriate input parameters of the metric you are looking for its corresponding monitoring data:");
	}


//---------------begin of the connection---------------
	@RequestMapping("/network")
	public @ResponseBody Network network(
		@RequestParam(value="authentication", required=false, defaultValue="Error") String authentication, 
		@RequestParam(value="hostid", required=false, defaultValue="") String hostid, 
		@RequestParam(value="metricType", required=false, defaultValue="" ) String name) 
		throws  FileNotFoundException, UnsupportedEncodingException, IOException{

	 	Properties props = new Properties();
		FileInputStream fis = new FileInputStream("properties.xml");
 	  	//loading properites from properties file
   		props.loadFromXML(fis);
	
		String server_ip =  props.getProperty("server_ip");
   		String ZABBIX_API_URL = "http://" + server_ip + "/api_jsonrpc.php"; // 1.2.3.4 is your zabbix_server_ip
		
		PutMethod putMethod = new PutMethod(ZABBIX_API_URL);

		// content-type is controlled in api_jsonrpc.php, so set it like this
		putMethod.setRequestHeader("Content-Type", "application/json-rpc"); 
		
		String request = "";

		if (name.equals("latency")) 
			request = setRequest("icmppingsec", authentication, hostid);
		else if (name.equals("packetloss"))
			request = setRequest("icmppingloss", authentication, hostid);
		else 	request = setRequest("net", authentication, hostid);

		putMethod.setRequestBody(request); // put the json object as input stream into request body 

		return networkResponse(hostid, putMethod, name);
	}
//--------------end of the connection------------------
}

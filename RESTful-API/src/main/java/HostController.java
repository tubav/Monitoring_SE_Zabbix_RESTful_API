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

 This class is aimed to achieve the goal of getting host information from 
 zabbix.
 
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
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

import java.util.Properties;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PutMethod;

@Controller
public class HostController{
    private final AtomicLong counter = new AtomicLong();

//---------------This Part starts the Zabbix connection---------------

    @RequestMapping("/hosts")
    public @ResponseBody Host host(
		@RequestParam(value="authentication", required=false, defaultValue="") String authentication) throws  FileNotFoundException, UnsupportedEncodingException, IOException {

	 	Properties props = new Properties();
    		FileInputStream fis = new FileInputStream("properties.xml");
    		//loading properites from properties file
    		props.loadFromXML(fis);

		String server_ip =  props.getProperty("server_ip");
    		String ZABBIX_API_URL = "http://" + server_ip + "/api_jsonrpc.php"; // 1.2.3.4 is your zabbix_server_ip

		HttpClient client = new HttpClient();
		
		PutMethod putMethod = new PutMethod(ZABBIX_API_URL);
		// content-type is controlled in api_jsonrpc.php, so set it like this
		putMethod.setRequestHeader("Content-Type", "application/json-rpc"); 
		// create json object for apiinfo.version 
		JSONParser parser=new JSONParser();
		JSONObject jsonObj = new JSONObject();
		JSONArray list = new JSONArray();
		jsonObj.put("jsonrpc","2.0");
		jsonObj.put("method","host.get");
		JSONObject params = new JSONObject();
		params.put("output","extend");
		jsonObj.put("params",params);
		jsonObj.put("auth", authentication);// todo
		jsonObj.put("id",new Integer(1));
		
		putMethod.setRequestBody(jsonObj.toString()); // put the json object as input stream into request body 
		
		String loginResponse = "";
		
		try {
			client.executeMethod(putMethod); // send to request to the zabbix api
			
			loginResponse = putMethod.getResponseBodyAsString(); // read the result of the response

                	Object obj = parser.parse(loginResponse);
                	JSONObject obj2 = (JSONObject) obj;
                	String jsonrpc= (String) obj2.get("jsonrpc");
                	JSONArray array = (JSONArray) obj2.get("result");

	                for (int i = 0; i < array.size(); i++){
       		                JSONObject tobj = (JSONObject) array.get(i);

				JSONObject objret = new JSONObject();
				
				objret.put("hostid",tobj.get("hostid"));
				objret.put("hostName",tobj.get("host"));
				
				list.add(objret);
			}
			return new Host(list);	
			
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException pe){
			pe.printStackTrace();
		}	
		return new Host("Error: please provide the appropriate input parameters of the metric you are looking for its corresponding monitoring data:");	
	}
//--------------This part ends the Zabbix connection------------------
}

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

 This class is aimed to achieve the goal of getting authentication from 
 zabbix.
 
 ************************************************************************
*/

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

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
public class AuthenticationController {

    private final AtomicLong counter = new AtomicLong();

//---------------This Part starts the Zabbix connection---------------
    @RequestMapping("/authentication")
    public @ResponseBody Authentication authentication(
		@RequestParam(value="username", required=true, defaultValue="") String username, @RequestParam(value="password", required=true, defaultValue="") String password) throws JSONException, FileNotFoundException, UnsupportedEncodingException ,IOException {
	
    		Properties props = new Properties();
    		FileInputStream fis = new FileInputStream("properties.xml");
    		// loading properites from properties file
    		props.loadFromXML(fis);

		String server_ip =  props.getProperty("server_ip");
    		String ZABBIX_API_URL = "http://" + server_ip + "/api_jsonrpc.php"; // 1.2.3.4 is your zabbix_server_ip


		HttpClient client = new HttpClient();
		
		PutMethod putMethod = new PutMethod(ZABBIX_API_URL);
		putMethod.setRequestHeader("Content-Type", "application/json-rpc"); // content-type is controlled in api_jsonrpc.php, so set it like this
		
		// create json object for apiinfo.version 
		JSONObject jsonObj=new JSONObject("{\"jsonrpc\":\"2.0\",\"method\":\"user.authenticate\",\"params\":{\"user\":\"" + username + "\",\"password\":\"" + password + "\"},\"auth\": null,\"id\":0}");
		
		putMethod.setRequestBody(jsonObj.toString()); // put the json object as input stream into request body 
		
		
		String loginResponse = "";
		
		try {
			client.executeMethod(putMethod); // send to request to the zabbix api
			
			loginResponse = putMethod.getResponseBodyAsString(); // read the result of the response
			
			// Work with the data using methods like...
			JSONObject obj = new JSONObject(loginResponse); 
			String result = obj.getString("result");
			System.out.println(result);

			return new Authentication(String.format(result));	
			
		} catch (HttpException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return new Authentication();	
	}
//--------------This part ends the Zabbix connection------------------
}


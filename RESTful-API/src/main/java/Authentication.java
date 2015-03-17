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

# This defines the structure of the class AuthenticationController.java.
 
*************************************************************************
*/

public class Authentication {

    private final String result;
    private final String status;

    public Authentication(){
	this.result = "";
	this.status = "Please provide the appropriate input parameters of the metric you are looking for its corresponding monitoring data:\n";
    }

    public Authentication(String result) {
        this.result = result;
	this.status = "ok";
    }

    public String getResult() {
        return result;
    }
  
    public String getStatus(){
	return status;
    }
}

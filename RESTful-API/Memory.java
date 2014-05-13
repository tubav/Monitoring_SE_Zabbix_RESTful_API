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

 This defines the structure of the class MemoryController.java.
 
 ************************************************************************
*/



public class Memory{
    private final String lastvalue;
    private final String timestamp;
    private final String metricType;
    private final String hostid;

    public Memory(String hostid, String metricType, String lastvalue, String timestamp) {
	this.hostid = hostid;
	this.lastvalue = lastvalue;
	this.timestamp = timestamp;
	this.metricType = metricType;
    }

    public String getLastValue(){
	return lastvalue;
    }

    public String getTimestamp(){
	return timestamp;
    }

    public String getmetricType(){
	return metricType; 
    }

    public String getHostid(){
	return hostid;
    }
}

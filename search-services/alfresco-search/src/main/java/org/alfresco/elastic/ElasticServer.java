/*
 * #%L
 * Alfresco Search Services
 * %%
 * Copyright (C) 2005 - 2020 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.alfresco.elastic;

import java.io.IOException;

import org.alfresco.solr.config.ConfigUtil;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an ElasticServer to be used for indexing or searching operations.
 * 
 * The implementation is based in the Elastic Rest Client.
 * 
 * @author aborroy
 *
 */
public class ElasticServer 
{
	
    protected final static Logger LOGGER = LoggerFactory.getLogger(ElasticServer.class);
	
	public static final String ELASTIC_PROTOCOL = "elastic.protocol";
	public static final String ELASTIC_HOST = "elastic.host";
	public static final String ELASTIC_PORT = "elastic.port";
	
	String protocol;
	String host;
	int port;
	
	public ElasticServer()
	{
		this.protocol = ConfigUtil.locateProperty(ELASTIC_PROTOCOL, null);
		this.host = ConfigUtil.locateProperty(ELASTIC_HOST, null);
		String portString = ConfigUtil.locateProperty(ELASTIC_PORT, null);
		if (portString != null)
		{
			this.port = Integer.valueOf(portString);
		}
		
	}
	
	/**
	 * Check if an Index exists in Elastic Server by name
	 * @param indexName Name of the index
	 * @return true when the index exists, false otherwise
	 */
	public boolean existsIndex(String indexName)
	{
		boolean exists = false;
		
		RestClient restClient = RestClient.builder(new HttpHost(host, port, protocol)).build();
		Request request = new Request("GET", "/alfresco");
		try 
		{
			Response response = restClient.performRequest(request);
			exists = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
			restClient.close();
		} 
		catch (IOException e) 
		{
			LOGGER.error("Elastic Server " + protocol + "//" + host + ":" + port + " is not reachable!", e);
		}
		return exists;
	}
	
	/**
	 * Create a new Elastic Index with name and mappings (JSON Schema)
	 * @param indexName Name of the index to be created
	 * @param jsonMapping JSON mapping properties and types for Lucene Schema
	 * @return true when the index is created, false otherwise
	 */
	public boolean createIndex(String indexName, String jsonMapping)
	{
		boolean success = false;

		RestClient restClient = RestClient.builder(new HttpHost(host, port, protocol)).build();
		Request request = new Request("PUT", "/alfresco");
		request.setJsonEntity(jsonMapping);
		try 
		{
			Response response = restClient.performRequest(request);
			success = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
			restClient.close();
		} 
		catch (IOException e) 
		{
			LOGGER.error("Elastic Server " + protocol + "//" + host + ":" + port + " is not reachable!", e);
		}
		return success;
	}
	
}

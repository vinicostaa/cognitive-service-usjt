package br.usjt.congnitive.spring.service;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.usjt.congnitive.spring.dao.FaceDAO;
import br.usjt.congnitive.spring.model.Face;

public class FaceServiceImpl implements FaceService {

	private final String DEFAULT_API_ROOT = "https://brazilsouth.api.cognitive.microsoft.com/face/v1.0";
	private final String FindSimilarsQuery = "findsimilars";
	private final String personGroupId = "usjt-pi";
	private final String DetectQuery = "detect";
	private final String PersistedFacesQuery = "persistedfaces";
	private final String subscriptionKey = "849ef8884bb04ca48e71abb5af9d5541";
	private final String PersonGroupsQuery = "persongroups";
	private final String PersonsQuery = "persons";
	private final String IdentifyQuery = "identify";
	
	private FaceDAO faceDAO;
	

	public void setFaceDAO(FaceDAO faceDAO) {
		this.faceDAO = faceDAO;
	}

	/**
	Identifica atráves do faceId se a face está cadastrada na lista (usjt-pi)
	@param faceIds - lista de facesIds que serão identificadas pela Azure
	@return Lista de faces indentificadas
	@author Grupo 2
	*/
	@Override
	public ArrayList<Face> Identify(ArrayList<String> faceIds) {
		HttpClient httpclient = HttpClients.createDefault();

		try {
			// Parameters and Headers
			URIBuilder builder = new URIBuilder(DEFAULT_API_ROOT + "/" + IdentifyQuery);
			HttpPost request = new HttpPost(builder.build());
			request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
			request.addHeader("content-type", "application/json");

			// Request body
			JSONObject json = new JSONObject();
			json.put("confidenceThreshold", 0.5f);
			json.put("faceIds", faceIds);
			json.put("personGroupId", this.personGroupId);
			json.put("maxNumOfCandidatesReturned", 10);

			StringEntity params = new StringEntity(json.toString());

			request.setEntity(params);

			HttpEntity entity = httpclient.execute(request).getEntity();

			if (entity != null) {
				String jsonResult = EntityUtils.toString(entity);
				System.out.println(jsonResult);
				Type listType = new TypeToken<List<Face>>() {
				}.getType();
				ArrayList<Face> faces = new Gson().fromJson(jsonResult, listType);
				return faces;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}

		return null;
	}
	
	/**
	Realizada a leitura do rosto e retorna um faceId para ela
	@param imageStream - Foto a ser detectada
	@param returnFaceId - A azure deve retornar o faceId
	@return FaceId da Foto.
	@author Grupo 2
	*/
	@Override
	public String Detect(File imageStream, boolean returnFaceId, boolean returnFaceLandmarks) {
		HttpClient httpclient = HttpClients.createDefault();

		try {
			// Parameters and Headers
			URIBuilder builder = new URIBuilder(DEFAULT_API_ROOT + "/" + DetectQuery);
			builder.setParameter("returnFaceId", String.valueOf(returnFaceId));
			HttpPost request = new HttpPost(builder.build());
			request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
			// Request body
			request.setEntity(new FileEntity(imageStream, ContentType.APPLICATION_OCTET_STREAM));
			HttpEntity entity = httpclient.execute(request).getEntity();

			if (entity != null) {
				String json = EntityUtils.toString(entity);
				Type listType = new TypeToken<List<Face>>() {
				}.getType();
				ArrayList<Face> c = new Gson().fromJson(json, listType);
				return c.get(0).getFaceId();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	/**
	Procura por rostos similares.
	@param faceId - faceId da foto a ser detectada.
	@param maxNumOfCandidatesReturned - O máximo de canditados que a azure deve retornar
	@return Faces detectas.
	@author Grupo 2
	*/
	@Override
	public List<Face> FindSimilar(String faceId, int maxNumOfCandidatesReturned) {
		HttpClient httpclient = HttpClients.createDefault();

		try {
			// Parameters and Headers
			URIBuilder builder = new URIBuilder(DEFAULT_API_ROOT + "/" + FindSimilarsQuery);

			HttpPost request = new HttpPost(builder.build());
			request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
			request.addHeader("content-type", "application/json");

			// Request body
			JSONObject json = new JSONObject();
			json.put("faceId", faceId.toString());
			json.put("faceListId", this.personGroupId);
			json.put("maxNumOfCandidatesReturned", maxNumOfCandidatesReturned);
			json.put("mode", "matchPerson");

			StringEntity params = new StringEntity(json.toString());

			request.setEntity(params);

			HttpEntity entity = httpclient.execute(request).getEntity();

			if (entity != null) {
				String jsonResult = EntityUtils.toString(entity);
				Type listType = new TypeToken<List<Face>>() {
				}.getType();
				ArrayList<Face> faces = new Gson().fromJson(jsonResult, listType);
				return faces;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}
	
	/**
	Adiciona Face a um grupo de faces, onde cada pessoa representa um grupo de faces.
	@param personId - personId do grupo de Person
	@param userData - Descicao da Face
	@param imageStream - A foto a ser adiconada
	@return Retorna face cadastrada.
	@author Grupo 2
	*/
	@Override
	public Face AddPersonFaceInPersonGroupAsync(String personId, String userData, File imageStream ) {
		String query = this.DEFAULT_API_ROOT + "/" + this.PersonGroupsQuery + "/" + personGroupId + "/" + this.PersonsQuery + 
				"/" + personId + "/" + PersistedFacesQuery + "?userData=" + userData.replaceAll("\\s+", "");
		
		HttpClient httpclient = HttpClients.createDefault();
		
	

		try {
			// Parameters and Headers
			URIBuilder builder = new URIBuilder(query);

			HttpPost request = new HttpPost(builder.build());
			request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

			// Request body
			request.setEntity(new FileEntity(imageStream, ContentType.APPLICATION_OCTET_STREAM));

			HttpEntity entity = httpclient.execute(request).getEntity();

			if (entity != null) {
				String json = EntityUtils.toString(entity);
				System.out.println(json);
				Face c = new Gson().fromJson(json, Face.class);
				return c;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	/**
	Cria um grupo de faces.
	@param name - nome da pessoa 
	@param userData - Descicao da pessoa
	@return Retorna o id do PersonGroup criada.
	@author Grupo 2
	*/
	@Override
	public Face CreatePersonGroup(String name, String userData) {
		String url = this.DEFAULT_API_ROOT + "/" + PersonGroupsQuery + "/" + personGroupId + "/" + PersonsQuery;
		
		HttpClient httpclient = HttpClients.createDefault();

		try {
			// Parameters and Headers
			URIBuilder builder = new URIBuilder(url);
			HttpPost request = new HttpPost(builder.build());
			request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
			request.addHeader("content-type", "application/json");

			// Request body
			JSONObject json = new JSONObject();
			json.put("name", name);
			json.put("userData", userData);

			StringEntity params = new StringEntity(json.toString());

			request.setEntity(params);
 
			HttpEntity entity = httpclient.execute(request).getEntity();

			if (entity != null) {
				String jsonResult = EntityUtils.toString(entity);
				System.out.println(jsonResult);
				Face c = new Gson().fromJson(jsonResult, Face.class);
				return c;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
		
	}
	
	
	@Override
	public Face AddFaceToFaceList(File imageStream, String userData) {
		HttpClient httpclient = HttpClients.createDefault();

		try {
			// Parameters and Headers
			URIBuilder builder = new URIBuilder(DEFAULT_API_ROOT + "/" + "persongroups" + "/" + this.personGroupId + "/"+
				"persons"	+ PersistedFacesQuery + "?userData=" + userData.replaceAll("\\s+", ""));

			HttpPost request = new HttpPost(builder.build());
			request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

			// Request body
			request.setEntity(new FileEntity(imageStream, ContentType.APPLICATION_OCTET_STREAM));

			HttpEntity entity = httpclient.execute(request).getEntity();

			if (entity != null) {
				String json = EntityUtils.toString(entity);
				Face c = new Gson().fromJson(json, Face.class);
				return c;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	/**
	Retorna Face pelo seu Id
	@param id - id Face 
	@return Retorna Face encontrada.
	@author Grupo 2
	*/
	@Override
	@Transactional
	public Face getFaceById(int id) {
		return this.faceDAO.getFaceById(id);
	}

	/**
	Retorna Face pelo seu ClienteId
	@param id - idCliente Face 
	@return Retorna Face encontrada.
	@author Grupo 2
	*/
	@Override
	@Transactional
	public Face getFaceByClientId(int id) {
		return this.faceDAO.getFaceByClientId(id);
	}

	/**
	Treina a api da Azure para detectar os rostos.
	@author Grupo 2
	*/
	@Override
	public boolean train() {
		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			String url = DEFAULT_API_ROOT + "/" + PersonGroupsQuery + "/" + personGroupId + "/train";
			URI uri = new URIBuilder(url)
		            .build();

			HttpPost request = new HttpPost(uri);
			request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
			CloseableHttpResponse response = httpclient.execute(request);
		

			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}

}

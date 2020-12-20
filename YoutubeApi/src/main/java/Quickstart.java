
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.*;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Search;

import java.io.IOException;
import java.sql.*;

import java.util.*;


public class Quickstart {
	
	private static YouTube youtube ;
		
	private static void connectToYoutube() {
			try {
				youtube = setYoutube.getYouTubeService();
			}catch(IOException e) {
				System.out.println("veuillez v�rifier votre connexion internet !!! :(");
			}
		}
	
    public static void main(String[] args)   {
    	connectToYoutube();
        Scanner scan = new Scanner(System.in);
        String Date_debut="";
        String Date_fin ="";
        String Motcle="";
        char exit='n';
        do {
	        System.out.printf("Mot de recherche : ");
	        Motcle = scan.nextLine();
	        System.out.println("Etrer une plage de date :\n");
	        System.out.println("\tDate debut (yyyy-mm-dd) :");
	        Date_debut = scan.next();
	        System.out.println("\tDate fin (yyyy-mm-dd) :");
	        Date_fin =scan.next();
	        try{
	        	int id_recherche = toDatabase(Motcle, Date_debut, Date_fin); // enregistre la recherche dans la bass des donnees
	            SearchByVideo(Motcle,id_recherche ,Date_debut, Date_fin);
	        }catch (GoogleJsonResponseException e) {
	            e.printStackTrace();
	            System.err.println("There was a service error: " +
	                e.getDetails().getCode() + " : " + e.getDetails().getMessage());
	        } catch (Throwable t) {
	            t.printStackTrace();
	        }
	     System.out.printf("Do you want to search again ? y/n :");
	     exit = scan.next().charAt(0);
	     scan.nextLine();
        }while(exit != 'n');
        System.out.println("Program shutdown :)");
        Database.getDatabase().closeDatabase();
        scan.close();
    }
    

    public static void SearchByChannel(String query, String date_debut , String date_fin)throws IOException{

			 // préparation de la requete
             HashMap<String, String> parameters = new HashMap<>();
             parameters.put("part", "snippet");
             parameters.put("maxResults", "1");
             parameters.put("q", query);
             parameters.put("type", "channel");
             
             // composition de requete
             YouTube.Search.List searchListByKeywordRequest = youtube.search().list(parameters.get("part").toString())
            		 .setPublishedAfter(DateTime.parseRfc3339(date_debut+"T00:00:00Z"))
            		 .setPublishedBefore(DateTime.parseRfc3339(date_fin+"T00:00:00Z"));
             
             if (parameters.containsKey("maxResults")) {
                    searchListByKeywordRequest.setMaxResults(Long.parseLong(parameters.get("maxResults").toString()));
                }

             if (parameters.containsKey("q") && parameters.get("q") != "") {
                    searchListByKeywordRequest.setQ(parameters.get("q").toString());
                }

            if (parameters.containsKey("type") && parameters.get("type") != "") {
                    searchListByKeywordRequest.setType(parameters.get("type").toString());
                }
            
                //Execution de la requete
            SearchListResponse response = searchListByKeywordRequest.execute();
            
                //Resultat de la requete exécutée
            List<SearchResult> results = response.getItems();

                //Pas de resultat 
            if(results.isEmpty()){
                System.out.println();
                System.out.println("Aucun resultat correspondant a votre recherche !");
                System.out.println();
            }else{
                 for (SearchResult result : results) {
                  ChannelA channel = new ChannelA(result.getSnippet().getChannelId(),result.getSnippet().getTitle());
                  VideoA.todatabase_channel(channel);
                  channel.Afficher();
                } 
             }   
    }

    public static void SearchByVideo(String query, int id_recherche, String date_debut , String date_fin) throws IOException{
    	
        
    	
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("part", "snippet");
        parameters.put("maxResults", "5");
        parameters.put("q", query);
        parameters.put("type", "video");

        YouTube.Search.List searchListByKeywordRequest = youtube.search().
        list(parameters.get("part").toString())
        .setPublishedAfter(DateTime.parseRfc3339(date_debut+"T00:00:00Z"))
        .setPublishedBefore(DateTime.parseRfc3339(date_fin+"T00:00:00Z"));
        if (parameters.containsKey("maxResults")) {
            searchListByKeywordRequest.setMaxResults(Long.parseLong(parameters.get("maxResults").toString()));
        }
        if (parameters.containsKey("q") && parameters.get("q") != "") {
            searchListByKeywordRequest.setQ(parameters.get("q").toString());
        }
        if (parameters.containsKey("type") && parameters.get("type") != "") {
            searchListByKeywordRequest.setType(parameters.get("type").toString());
        }
        SearchListResponse response = searchListByKeywordRequest.execute();
     
        List<SearchResult> results = response.getItems();

                //Pas de resultat 
            if(results.isEmpty()){
                System.out.println();
                System.out.println("Aucun resultat correspondant a votre recherche !");
                System.out.println();
            }else{
                 for (SearchResult result : results) {
                  VideoA video = new VideoA(result.getId().getVideoId());
                  toDatabaseresultat(video.getVideoid(), id_recherche);
                  video.AfficherVideoDeteails();
                  video.AfficherVideoComments();
                } 
             }   
    }
    
    // todatabaseRecherche
    static int toDatabase(String Motcle, String date_debut , String date_fin)  {
 
    	String query ="insert into Recherche (date_recherchce,date_debut,date_fin,query) "
    			+ "values(?,date'"+date_debut+"',date'"+date_fin+"',?)";
    	int id_recherche = 0;
    	try {
    		Database database = Database.getDatabase();
    		PreparedStatement pstat = database.getPreparedS(query);
    		pstat.setDate(1, getCurrentDate());
    		pstat.setString(2,Motcle);
    		pstat.executeUpdate(); // return number of rows inserted
    		ResultSet res = database.getstatment().executeQuery("select Max(id_recherche) from recherche");
    		while(res.next()) {
    			id_recherche = res.getInt(1);
    		}
    		res.close();
    	}catch (Exception e){
    		System.out.println(e);
    	}
    	return id_recherche;
    }
    
    
    // convert the date
    private static java.sql.Date getCurrentDate() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Date(today.getTime());
    }
    
    static void toDatabaseresultat(String id_video , int id_recherche) {
    	String query = "insert into resultat values ('"+id_video+"',"+id_recherche+")";
    	try {
    		Database database = Database.getDatabase();
    		PreparedStatement pstat = database.getPreparedS(query);
    		pstat.executeUpdate();
    	}catch (Exception e){
       		System.out.println(e);
        }
    }

}




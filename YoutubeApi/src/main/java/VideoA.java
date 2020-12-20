import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.*;
import com.google.api.services.youtube.YouTube;


import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.sql.*;

public class VideoA {
    private String Videoid;
    private String Channelid;
    private DateTime publishat;
    private String Title;
    private BigInteger Viewcount;
    private BigInteger Likecount;
    private BigInteger Dislikecount;
    private BigInteger Commentcount;
    private CommentA CommentOfVideo =null;
    
    private static YouTube youtube ;
	
	private static void connectToYoutube() {
			try {
				youtube = setYoutube.getYouTubeService();
			}catch(IOException e) {
				System.out.println("veuillez vérifier votre connexion internet !!! :(");
			}
		}

    public VideoA (String id ) throws IOException{
        this.Videoid = id;
  
        connectToYoutube();

        YouTube.Videos.List videosListByIdRequest = youtube.videos().list("snippet,statistics");
        videosListByIdRequest.setId(id);

        VideoListResponse response = videosListByIdRequest.execute();
        Video video = response.getItems().get(0);
        this.Channelid = video.getSnippet().getChannelId();
        this.publishat = video.getSnippet().getPublishedAt();
        this.Title =video.getSnippet().getTitle();
        this.Viewcount =video.getStatistics().getViewCount();
        this.Likecount =video.getStatistics().getLikeCount();
        this.Dislikecount = video.getStatistics().getDislikeCount();
        this.Commentcount =  video.getStatistics().getCommentCount();
        ChannelA videochannel = new ChannelA(this.Channelid);
        todatabase_channel(videochannel);
        toDatabaseVideo(this);
        this.CommentOfVideo = new CommentA(id,this.Channelid);
    }

    
    public String getVideoid() {
		return Videoid;
	}


	public void setVideoid(String videoid) {
		Videoid = videoid;
	}


	public String getChannelid() {
		return Channelid;
	}


	public void setChannelid(String channelid) {
		Channelid = channelid;
	}


	public DateTime getPublishat() {
		return publishat;
	}


	public void setPublishat(DateTime publishat) {
		this.publishat = publishat;
	}


	public String getTitle() {
		return Title;
	}


	public void setTitle(String title) {
		Title = title;
	}


	public BigInteger getViewcount() {
		return Viewcount;
	}


	public void setViewcount(BigInteger viewcount) {
		Viewcount = viewcount;
	}


	public BigInteger getLikecount() {
		return Likecount;
	}


	public void setLikecount(BigInteger likecount) {
		Likecount = likecount;
	}


	public BigInteger getDislikecount() {
		return Dislikecount;
	}


	public void setDislikecount(BigInteger dislikecount) {
		Dislikecount = dislikecount;
	}


	public void AfficherVideoDeteails(){
        System.out.println("---------------"+this.Title+"-----------------");
        System.out.println(
        "\nVideo's ID   : "+this.Videoid+
        "\nChannel ' ID : "+this.Channelid+
        "\nPublishAt    : "+this.publishat+
        "\nViewcount    : "+this.Viewcount+
        "\nLike count   : "+this.Likecount+
        "\nDislike count: "+this.Dislikecount+
        "\nComment count: "+this.Commentcount);
        System.out.println("------------------------------------------------------------");
        System.out.println();
    }

    public BigInteger getCommentcount() {
		return Commentcount;
	}


	public void setCommentcount(BigInteger commentcount) {
		Commentcount = commentcount;
	}


	public void AfficherVideoComments(){
        if(CommentOfVideo==null || CommentOfVideo.CommentsVideo.isEmpty()){
            System.out.println("La video avec l'identifiant : "+Videoid+" n'a pas de commentaire !");
        }else{
                System.out.println("\n==================  Video Comments ==================\n");
                for (CommentToplevel commenttop : CommentOfVideo.commenttoplevel) {
                    System.out.println("\n==================  Comments ==================\n");
                    System.out.println("  - Comment's id             : " +commenttop.getCommentid());
                    System.out.println("  - Auteur                   : " +commenttop.getAuteur());
                    System.out.println("  - Auteur_Img               : " +commenttop.getAuteur_Img());
                    System.out.println("  - Comment                  : " +commenttop.getTextDispString());
                    System.out.println("  - Likecount of the comment : " +commenttop.getLikeCount());
                    System.out.println("  - Video_id                 : " +commenttop.getVideo_id());
                    System.out.println("  - Channel_id               : " +commenttop.getChannel_id());
                    System.out.println("  - Parent_id                : " +commenttop.getParent_id());
                    System.out.println("\n-------------------------------------------------------------\n");
                    if(commenttop.getReplies() != null && !commenttop.getReplies().isEmpty()){
                        System.out.println("\n\t==================  Replies Comments ==================\n");
                        for (CommentToplevel replie : commenttop.getReplies()) {
                            System.out.println("\t - Replie Comment's id      : "+replie.getCommentid());
                            System.out.println("\t - Auteur                   : "+replie.getAuteur());
                            System.out.println("\t - Auteur_Img               : "+replie.getAuteur_Img());
                            System.out.println("\t - Comment                  : "+replie.getTextDispString());
                            System.out.println("\t - Likecount of the comment : "+replie.getLikeCount());
                            System.out.println("\t - Video_id                 : "+replie.getVideo_id());
                            System.out.println("\t - Channel_id               : "+replie.getChannel_id());
                            System.out.println("\t - Parent_id                : "+replie.getParent_id());
                        }
                        System.out.println("\n\t-------------------------------------------------------------\n");
                    }
                }
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
	
	static void todatabase_channel(ChannelA ch) {
		String query ="insert into channel (id_channel,Titre_channel,"
				+ "Subscribercount,Viewcount,videocount) values(?,?,?,?,?)";
    	try {
    		Database database = Database.getDatabase();
    		ResultSet res = database.getstatment().executeQuery("select id_channel from channel where id_channel='"+ch.getId()+"'");
    		PreparedStatement pstat;
    		if(res.next()) {
    			query = "update channel set Subscribercount=? , viewcount=? , videocount =?"
    					+ "where id_channel= ?";
    			pstat = database.getPreparedS(query);
    			pstat.setLong(1, ch.getSubscribercount().longValue());
        		pstat.setLong(2, ch.getViewcount().longValue());
        		pstat.setLong(3, ch.getVideocount().longValue());
        		pstat.setString(4, ch.getId());
        		pstat.executeUpdate();
    		}else {
    			pstat = database.getPreparedS(query);
        		pstat.setString(1, ch.getId());
        		pstat.setString(2, ch.getTitle());
        		pstat.setLong(3, ch.getSubscribercount().longValue());
        		pstat.setLong(4, ch.getViewcount().longValue());
        		pstat.setLong(5, ch.getVideocount().longValue());
        		pstat.executeUpdate(); // return number of rows inserted
    		}
    		res.close();
    }catch (Exception e){
   		System.out.println(e);
    }
	}
	
	//todatebase VideoAndConcerner
    static void toDatabaseVideo(VideoA v ) {
    	
    	String query ="insert into video (ID_video,Titre,publishedAt,viewcount,"
    			+ "likecount,dislikecount,commentcount,id_channel)"
    			+ "values(?,?,date'"+v.getPublishat().toString().substring(0,10)+"',?,?,?,?,?)";
    	try {
    		Database database = Database.getDatabase();
    		ResultSet res = database.getstatment().executeQuery("select id_video from video where id_video='"+v.getVideoid()+"'");
    		PreparedStatement pstat ;
    		if(res.next()) {
    			query = "update video set viewcount=?, likecount=? , dislikecount=? ,commentcount= ? "
    					+ "where id_video= ?";
    			pstat = database.getPreparedS(query);
    			pstat.setLong(1, v.getViewcount().longValue());
        		pstat.setLong(2, v.getLikecount().longValue());
        		pstat.setLong(3, v.getDislikecount().longValue());
        		pstat.setLong(4, v.getCommentcount().longValue());
        		pstat.setString(5, v.getVideoid());
        		pstat.executeUpdate();
    		}else {
    			pstat = database.getPreparedS(query);
        		pstat.setString(1, v.getVideoid());
        		pstat.setString(2, v.getTitle());
        		pstat.setLong(3, v.getViewcount().longValue());
        		pstat.setLong(4, v.getLikecount().longValue());
        		pstat.setLong(5, v.getDislikecount().longValue());
        		pstat.setLong(6, v.getCommentcount().longValue());
        		pstat.setString(7, v.getChannelid());
        		pstat.executeUpdate(); // return number of rows inserted
    		}
    		res.close();
    }catch (Exception e){
   		System.out.println(e);
    }
   }
}


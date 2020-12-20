
import com.google.api.services.youtube.model.*;



import com.google.api.services.youtube.YouTube;


import java.io.IOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class CommentA extends setYoutube{
	
    public List<CommentThread> CommentsVideo = null;
    public List<CommentToplevel> commenttoplevel = new ArrayList<>();
    
    private static YouTube youtube ;
	
	private static void connectToYoutube() {
			try {
				youtube = setYoutube.getYouTubeService();
			}catch(IOException e) {
				System.out.println("veuillez vérifier votre connexion internet !!! :(");
			}
		}

    public CommentA(String id,String id_channel) throws IOException{
        connectToYoutube();

        YouTube.CommentThreads.List commentThreadsListByVideoIdRequest = youtube.commentThreads().list("snippet,replies");

        commentThreadsListByVideoIdRequest.setVideoId(id);
        commentThreadsListByVideoIdRequest.setMaxResults(10l).setTextFormat("plainText");
        CommentThreadListResponse response = commentThreadsListByVideoIdRequest.execute();
        this.CommentsVideo = response.getItems();
        CommentToplevel comtop,rep;
        for (CommentThread  commenttop : this.CommentsVideo) {
            Comment comment = commenttop.getSnippet().getTopLevelComment();
            comtop =new CommentToplevel(
                    comment.getId(), 
                    comment.getSnippet().getAuthorDisplayName(),
                    comment.getSnippet().getAuthorProfileImageUrl(),
                    comment.getSnippet().getTextDisplay(), 
                    comment.getSnippet().getLikeCount(),
                    id_channel,
                    comment.getSnippet().getVideoId(),
                    comment.getId()
                );
            toDatabase_comment(comtop);
            commenttoplevel.add(comtop);
            List<Comment> replies = getreplies(commenttop.getId());
            if(replies != null && !replies.isEmpty()) {
            	for (Comment replie : replies) {
    				rep = new CommentToplevel(
    					replie.getId(), 
    					replie.getSnippet().getAuthorDisplayName(),
    					replie.getSnippet().getAuthorProfileImageUrl(),
    					replie.getSnippet().getTextDisplay(), 
    					replie.getSnippet().getLikeCount(),
    					id_channel,
    	                comment.getSnippet().getVideoId(),
    					replie.getSnippet().getParentId()
    						);
    				if(rep != null) {
    					comtop.getReplies().add(rep);
    					toDatabase_comment(rep);
    				}
            	}
            }
        }
    }
    
    // getting replies by toplevel id
    private  List<Comment> getreplies(String parentid) throws IOException{
        connectToYoutube();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("part", "snippet");
        parameters.put("parentId", parentid);

        YouTube.Comments.List commentsListRequest = youtube.comments().list(parameters.get("part").toString());
        if (parameters.containsKey("parentId") && parameters.get("parentId") != "") {
            commentsListRequest.setParentId(parameters.get("parentId").toString());
        }

        CommentListResponse response = commentsListRequest.execute();
        List<Comment> replies = response.getItems();
        return replies;
    }
    
    //todatabaseCommentaire
    private static void toDatabase_comment(CommentToplevel c) {
    	String query = "insert into commentaire (comment_id,auteur_name,textdisplay,likecount,auteur_profile_image"
    			+ ",comment_id_Commentaire,ID_video,id_channel) values(?,?,?,?,?,?,?,?)";
    	try {
    		Database database = Database.getDatabase();
    		ResultSet res = database.getstatment().executeQuery("select comment_id from commentaire"
    				+ " where comment_id='"+c.getCommentid()+"'");
    		PreparedStatement pstat;
    		if(res.next()) {
    			query = "update commentaire set auteur_name=? , textdisplay=? , likecount =? , auteur_profile_image=?"
    					+ "where comment_id= ?";
    			pstat = database.getPreparedS(query);
    			pstat.setString(1, c.getAuteur());
    			pstat.setString(2, c.getTextDispString());
    			pstat.setLong(3, c.getLikeCount());
    			pstat.setString(4, c.getAuteur_Img());
    			pstat.setString(5, c.getCommentid());
        		pstat.executeUpdate();
    		}else {
    			pstat = database.getPreparedS(query);
        		pstat.setString(1, c.getCommentid());
        		pstat.setString(2, c.getAuteur());
        		pstat.setString(3, c.getTextDispString());
        		pstat.setLong(4, c.getLikeCount());
        		pstat.setString(5, c.getAuteur_Img());
        		pstat.setString(6, c.getParent_id());
        		pstat.setString(7, c.getVideo_id());
        		pstat.setString(8, c.getChannel_id());
        		pstat.executeUpdate(); // return number of rows inserted
    		}
    		res.close();
    }catch (Exception e){
   		System.out.println(e);
    }
    }
    
}

import com.google.api.services.youtube.model.*;


import java.util.*;

public class CommentToplevel{
    private String Commentid ;
    private String Auteur;
    private	String Auteur_Img;
    private String textDisplay;
    private Long Likecount;
    private	String channel_id;
    private	String video_id;
    private String parent_id;
    private List<CommentToplevel> replies = null;

    public CommentToplevel(String Commentid,String Auteur,String Auteur_img,String textDisplay,Long likecount ,String channel_id,String video_id,String parent_id ){
        this.Commentid = Commentid;
        this.Auteur = Auteur;
        this.Auteur_Img =Auteur_img;
        this.textDisplay =textDisplay;
        this.Likecount =likecount;
        this.channel_id =channel_id;
        this.video_id = video_id;
        this.parent_id = parent_id;
    }

	public String getAuteur_Img() {
		return Auteur_Img;
	}

	public String getChannel_id() {
		return channel_id;
	}

	public String getVideo_id() {
		return video_id;
	}

	public String getParent_id() {
		return parent_id;
	}

	public String getCommentid(){
        return Commentid;
    }
    public String getAuteur(){
        return Auteur;
    }
    public String getTextDispString(){
        return textDisplay;
    }
    public Long getLikeCount(){
        return Likecount;
    }

    public List<CommentToplevel> getReplies(){
    	if(this.replies == null)
    		this.replies = new ArrayList<>();
        return replies;
    }

    public void setReplies(List<CommentToplevel> rep){
        this.replies =rep;
    }

}
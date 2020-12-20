import com.google.api.client.googleapis.json.GoogleJsonResponseException;


import com.google.api.services.youtube.model.*;
import com.google.api.services.youtube.YouTube;


import java.io.IOException;
import java.math.BigInteger;


public class ChannelA extends setYoutube{
    private String id;
    private String Title;
    private BigInteger viewcount;
    private BigInteger subscribercount ;
    private BigInteger videocount;
    
    private static YouTube youtube ;
	
	private static void connectToYoutube() {
			try {
				youtube = setYoutube.getYouTubeService();
			}catch(IOException e) {
				System.out.println("veuillez vérifier votre connexion internet !!! :(");
			}
		}
    
    public ChannelA(String id , String Title) throws IOException{
        this.id = id;
        this.Title =Title;
        connectToYoutube();
        YouTube.Channels.List channelsListByIdRequest = youtube.channels().list("statistics");
        channelsListByIdRequest.setId(id.toString());
        ChannelListResponse response = channelsListByIdRequest.execute();
        Channel channel = response.getItems().get(0);
        this.viewcount = channel.getStatistics().getViewCount();
        this.subscribercount = channel.getStatistics().getSubscriberCount();
        this.videocount = channel.getStatistics().getVideoCount();
    }
    
    public ChannelA(String id) throws IOException{
        this.id = id;
        connectToYoutube();
        YouTube.Channels.List channelsListByIdRequest = youtube.channels().list("snippet,statistics");
        channelsListByIdRequest.setId(id.toString());
        ChannelListResponse response = channelsListByIdRequest.execute();
        Channel channel = response.getItems().get(0);
        this.Title =channel.getSnippet().getTitle();
        this.viewcount = channel.getStatistics().getViewCount();
        this.subscribercount = channel.getStatistics().getSubscriberCount();
        this.videocount = channel.getStatistics().getVideoCount();
    }
    
    public void Afficher(){
        System.out.println("---------------"+this.Title+"-----------------");
        System.out.println(
        "\nChannel's ID : "+this.id+
        "\nSubscriber count : "+this.subscribercount+
        "\nVideo count: "+this.videocount+
        "\nViewcount : "+this.viewcount);
        System.out.println("-----------------------------------------------");
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public BigInteger getViewcount() {
		return viewcount;
	}

	public void setViewcount(BigInteger viewcount) {
		this.viewcount = viewcount;
	}

	public BigInteger getSubscribercount() {
		return subscribercount;
	}

	public void setSubscribercount(BigInteger subscribercount) {
		this.subscribercount = subscribercount;
	}

	public BigInteger getVideocount() {
		return videocount;
	}

	public void setVideocount(BigInteger videocount) {
		this.videocount = videocount;
	}

    
}
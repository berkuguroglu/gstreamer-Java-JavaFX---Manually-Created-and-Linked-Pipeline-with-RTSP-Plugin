package sample;

import com.sun.jna.Pointer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.AppSink;
import org.freedesktop.gstreamer.lowlevel.GstAPI;
import org.freedesktop.gstreamer.lowlevel.GstBufferAPI;
import org.freedesktop.gstreamer.lowlevel.GstBufferAPI.MapInfoStruct;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Pipe;
import java.util.ArrayList;



public class Main extends Application {

    Controller myController;
    private ArrayList<ImageView> imageViews;
    private ImageView imageView;
    private AppSink videosink;
    private static Pipeline pipe;
    private StringBuilder caps;
    private ImageContainer imageContainer, imageContainer_two;
    public Main() 
    {


        pipe = new Pipeline();
        Element source = ElementFactory.make("rtspsrc", "source");
        Element decodebin = ElementFactory.make("decodebin", "decoder");
        Element depay = ElementFactory.make("rtph264depay", "depay");
        Element converter = ElementFactory.make("autovideoconvert", "converter");
        videosink = new AppSink("sink");
        videosink.set("emit-signals", true);
        videosink.set("name", "sinkexample");
        AppSinkListener GstListener = new AppSinkListener();
        videosink.connect(GstListener);
        caps = new StringBuilder("video/x-raw, ");
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            caps.append("format=BGRx");
        } else {
            caps.append("format=xRGB");
        }
        videosink.setCaps(new Caps(caps.toString()));
        videosink.set("max-buffers", 10000);
        videosink.set("drop", true);
        imageView = new ImageView();
        imageContainer = GstListener.getImageContainer();
        imageContainer.addListener(new ChangeListener<Image>() {

            @Override
            public void changed(ObservableValue<? extends Image> observable, Image oldValue, final
            Image newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImage(newValue);
                    }
                });

            }
        });
        source.set("location", "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
        source.set("latency", 1000);
        pipe.getBus().connect(new Bus.ERROR() 
        {
            @Override
            public void errorMessage(GstObject source, int code, String message) {
                System.out.println(message);
            }
        });

        pipe.addMany(source, depay, decodebin, converter, videosink);
        source.connect(new Element.PAD_ADDED()
        {
            @Override
            public void padAdded(Element element, Pad pad)
            {
                try 
                {

                    System.out.print(Element.linkMany(element, depay, decodebin));
                }
                catch (PadLinkException ex)
                {
                    ex.printStackTrace();
                }
            }

        });
        decodebin.connect(new Element.PAD_ADDED()
        {
            @Override
            public void padAdded(Element element, Pad pad)
            {

                element.setCaps(new Caps(caps.toString()));
                System.out.println(element.link(converter));


            }
        });
        pipe.setState(State.PLAYING);
        converter.link(videosink); 
        Pipeline.linkMany(converter, videosink); 
        pipe.play();

    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader root = new FXMLLoader(getClass().getResource("sample.fxml"));
        myController = new Controller();
        root.setLocation(getClass().getResource("sample.fxml").toURI().toURL());
        root.setController((Controller)myController);
        AnchorPane p = root.load();
        myController.main_to_controller(imageView);
        primaryStage.setTitle("Real Time Streaming");
        Scene myScene = new Scene(p, p.getPrefWidth(), p.getPrefHeight());
        primaryStage.setResizable(false);
        primaryStage.setScene(myScene);
        primaryStage.show();

    }
    public static Pipeline get_pipeline()
    {
        return Main.pipe;
    }

    public static void main(String[] args)
    {

         Gst.init("myprogram", args);
         launch(args);


    }
}

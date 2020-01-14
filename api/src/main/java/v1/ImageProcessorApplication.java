package instagram2.imageprocessing.api.v1;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.kumuluz.ee.discovery.annotations.RegisterService;

@RegisterService
@ApplicationPath("/v1")
public class ImageProcessorApplication extends Application{
    
}
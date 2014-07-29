package detective.core.services;

import groovyx.gpars.group.PGroup;
import groovyx.gpars.group.DefaultPGroup;
import groovyx.gpars.scheduler.ResizeablePool;

import java.net.InetAddress;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;

import detective.common.httpclient.IdleConnectionMonitorThread;
import detective.common.trace.TraceRecorder;
import detective.common.trace.TraceRetriver;
import detective.common.trace.impl.TraceRecorderElasticSearchImpl;
import detective.common.trace.impl.TraceRetriverElasticSearchImpl;
import detective.core.config.DetectiveConfig;

public enum DetectiveFactory {

  INSTANCE;
  
  private final Logger logger = LoggerFactory.getLogger(DetectiveFactory.class);
  
  private final String machineName;
  private final TraceRecorder recorder;
  private final TraceRetriver retriver;
  
  private final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
  private final IdleConnectionMonitorThread idleConnectionMonitorThread = new IdleConnectionMonitorThread(cm);
  private final CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
  
  private final PGroup threadGroup;
  
  private DetectiveFactory(){
    if (getConfig().getBoolean("ElasticSearchServer.builtin")){
      
    }else{
//      ElasticSearchClientFactory.setHostName(getConfig().getString("ElasticSearchServer.host"));
//      ElasticSearchClientFactory.setPort(getConfig().getInt("ElasticSearchServer.port"));
//      ElasticSearchClientFactory.getTransportClient();
    }
    
    this.machineName = this.getInstanceId();
    
    recorder = new TraceRecorderElasticSearchImpl();
    
    retriver = new TraceRetriverElasticSearchImpl();
    
    int poolsize = getConfig().getInt("parallel.max_poolsize");
    threadGroup = new DefaultPGroup(new ResizeablePool(true, poolsize));
    
    cm.setMaxTotal(getConfig().getInt("httpclient.max_connections"));
    cm.setDefaultMaxPerRoute(getConfig().getInt("httpclient.max_connections_pre_site"));
    
    idleConnectionMonitorThread.setName("idle-connection-monitor-thread");
    idleConnectionMonitorThread.start();
  }
  
  public void shutdown(){
    try {
      threadGroup.shutdown();
    } catch (Exception e) {
      logger.error("Error to shutdown running thread pools", e);
    }
    
    idleConnectionMonitorThread.shutdown();
    
    try {
      cm.shutdown();
    } catch (Exception e) {
      logger.error("Error to shutdown http client thread pools", e);
    }
  }
  
  public Config getConfig(){
    return DetectiveConfig.getConfig();
  }
  
  public String getMachineName(){
    return machineName;
  }
  
  private String getInstanceId(){
    try {
      String hostName = getHostNameFromSystemEnv();
      if (hostName == null || hostName.length() == 0)
        hostName = InetAddress.getLocalHost().getHostAddress();
      
      if (hostName == null || hostName.length() == 0)
        hostName = InetAddress.getLocalHost().getHostName();
      
      return hostName;
    } catch (Exception e) {
      throw new RuntimeException("Can't read your host name, if you are in mac, please run sudo scutil –-set HostName yourname", e);
    }
  }
  
  private String getHostNameFromSystemEnv(){
    if (System.getProperty("os.name").startsWith("Windows")) {
        // Windows will always set the 'COMPUTERNAME' variable
        return System.getenv("COMPUTERNAME");
    } else {
        // If it is not Windows then it is most likely a Unix-like operating system
        // such as Solaris, AIX, HP-UX, Linux or MacOS.
  
        // Most modern shells (such as Bash or derivatives) sets the 
        // HOSTNAME variable so lets try that first.
        return System.getenv("HOSTNAME");
    }
  }

  public TraceRecorder getRecorder() {
    return recorder;
  }

  public TraceRetriver getRetriver() {
    return retriver;
  }

  public CloseableHttpClient getHttpClient() {
    return httpClient;
  }

  public PGroup getThreadGroup() {
    return threadGroup;
  }
}
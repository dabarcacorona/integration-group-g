package cl.corona.integrationgroupg.scheduler;

import cl.corona.integrationgroupg.service.intDownload;
import cl.corona.integrationgroupg.service.intDownloadIRCV;
import cl.corona.integrationgroupg.service.intUpload;
import cl.corona.integrationgroupg.service.intUploadIRCV;
import cl.corona.integrationgroupg.setService.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class scheduledTasks {

    private static final Logger LOG = LoggerFactory.getLogger(scheduledTasks.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    private intDownload intdownload;

    @Autowired
    private intUpload intupload;

    @Autowired
    private setDownload setdownload;

    @Autowired
    private setUpload setupload;

    @Autowired
    private SetDownloadCOMEX setdownloadcomex;

    @Autowired
    private SetUploadCOMEX setuploadcomex;

    @Autowired
    private intDownloadIRCV intdownloadircv;

    @Autowired
    private intUploadIRCV intuploadircv;



    @Scheduled(cron = "${cron.expression}")
    public void scheduledJob() throws InterruptedException, IOException {

        LOG.info("{} : Inicio transferencia de archivos",
                dateTimeFormatter.format(LocalDateTime.now()));

        setdownloadcomex.DownloadFile();
        setuploadcomex.UploadFile();

        setdownload.DownloadFile();
        setupload.UploadFile();

        intdownloadircv.DownloadFile();
        intuploadircv.UploadFile();

        intdownload.DownloadFile();
        intupload.UploadFile();

        LOG.info("{} : Fin transferencia de archivos",
                dateTimeFormatter.format(LocalDateTime.now()));

    }
}

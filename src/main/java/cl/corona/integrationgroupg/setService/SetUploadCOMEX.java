package cl.corona.integrationgroupg.setService;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Service
public class SetUploadCOMEX {

    @Value("${sftpdcmx.ip}")
    private String d_sftpip;

    @Value("${sftpdcmx.prt}")
    private int d_sftpprt;

    @Value("${sftpdcmx.usr}")
    private String d_sftpusr;

    @Value("${sftpdcmx.pss}")
    private String d_sftppss;

    @Value("${sftpdcmx.org}")
    private String d_sftporg;

    @Value("${sftpdcmx.dst_IRCVOC}")
    private String d_sftpdst_IRCVOC;

    @Value("${sftpdcmx.dst_OC}")
    private String d_sftpdst_OC;

    @Value("${name.file}")
    private String d_namefile;

    @Value("${separador.carpetas}")
    private String separador;

    @Value("${largo.archivo}")
    private int largo_archivo;

    private static final Logger LOG = LoggerFactory.getLogger(setUpload.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    String strDir = System.getProperty("user.dir");

    public void UploadFile() throws IOException {

        JSch jsch = new JSch();
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "password");
        jsch.setConfig(config);

        try {

            Session session = jsch.getSession(d_sftpusr, d_sftpip, d_sftpprt);
            session.setConfig("PreferredAuthentications", "password");
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(d_sftppss);
            session.connect();
            Channel channel = session.openChannel("sftp");
            ChannelSftp d_sftp = (ChannelSftp) channel;
            d_sftp.connect();

            final String path = strDir + separador + d_sftporg;
            //final String path = sftporg;

            File directory = new File(path);
            File[] fList = directory.listFiles();

            for (File file : fList) {

                String name = StringUtils.getFilename(file.getName());
                int end = name.indexOf("_");
                String sSubCadena = name.substring(0, end).toUpperCase();

                //if (sSubCadena.equals(d_namefile)) {
                switch (sSubCadena) {
                    //FLUJO IRCVOC
                    case "SDIRCVHDI":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            LOG.info("Uploading Files IRCVOC " + filename + " ---> " + d_sftpdst_IRCVOC);
                            d_sftp.put(filename, d_sftpdst_IRCVOC);
                            file.delete();
                            LOG.info("{} : Upload Ok", dateTimeFormatter.format(LocalDateTime.now()));

                        }
                        break;

                    case "SDIPMGHDI":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            LOG.info("Uploading Files OC " + filename + " ---> " + d_sftpdst_OC);
                            d_sftp.put(filename, d_sftpdst_OC);
                            file.delete();
                            LOG.info("{} : Upload Ok", dateTimeFormatter.format(LocalDateTime.now()));

                        }
                        break;
                }

            }

            d_sftp.exit();
            channel.disconnect();
            session.disconnect();

        } catch (JSchException e) {
            LOG.error("No se pudo realizar la conexión ,{}", e);
        } catch (SftpException e) {
            e.printStackTrace();
        }

    }
}

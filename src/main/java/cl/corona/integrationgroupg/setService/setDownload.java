package cl.corona.integrationgroupg.setService;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Vector;

@Service
public class setDownload {
    @Value("${sftpos.ip}")
    private String sftpip;

    @Value("${sftpos.prt}")
    private int sftpprt;

    @Value("${sftpos.usr}")
    private String sftpusr;

    @Value("${sftpos.pss}")
    private String sftppss;

    @Value("${sftpos.org_ITRF}")
    private String o_sftpdtn_ITRF;

    @Value("${sftpos.org_CC_AJUS}")
    private String o_sftpdtn_CCAJUS;

    @Value("${sftpos.org_IRCV}")
    private String o_sftpdtn_IRCV;

    @Value("${sftpos.dst}")
    private String sftpdst;

    @Value("${name.file}")
    private String namefile;

    @Value("${separador.carpetas}")
    private String separador;

    @Value("${largo.archivo}")
    private int largo_archivo;

    private static final Logger LOG = LoggerFactory.getLogger(setDownload.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    String strDir = System.getProperty("user.dir");

    public void DownloadFile() throws IOException {

        JSch jsch = new JSch();
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "password");
        jsch.setConfig(config);

        try {

            Session session = jsch.getSession(sftpusr, sftpip, sftpprt);
            session.setConfig("PreferredAuthentications", "password");
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(sftppss);
            session.connect();
            Channel channel = session.openChannel("sftp");
            ChannelSftp sftp = (ChannelSftp) channel;
            sftp.connect();

            final String pathdest = strDir + separador + sftpdst + separador;
            //final String path = sftporg;
            //LOG.info(path);

            Vector<ChannelSftp.LsEntry> entries2 = sftp.ls(o_sftpdtn_ITRF);

            //download all files (except the ., .. and folders) from given folder
            for (ChannelSftp.LsEntry en : entries2) {
                if (en.getFilename().equals(".") || en.getFilename().equals("..") || en.getAttrs().isDir()) {
                    continue;
                }

                String filename = StringUtils.getFilename(en.getFilename());
                //String sSubCadena = filename.substring(0, largo_archivo).toUpperCase();
                int end = filename.indexOf("_");
                String sSubCadena = filename.substring(0, end).toUpperCase();

                //LOG.info(sSubCadena);

                if (sSubCadena.equals("SDITRFDTI")) {
                    LOG.info("Downloading ITRF " + (o_sftpdtn_ITRF + en.getFilename()) + " ---> " + pathdest + en.getFilename());
                    sftp.get(o_sftpdtn_ITRF + en.getFilename(), pathdest + en.getFilename());
                    sftp.rm(o_sftpdtn_ITRF + en.getFilename());
                    LOG.info("{} : Download Ok", dateTimeFormatter.format(LocalDateTime.now()));
                }

            }

            Vector<ChannelSftp.LsEntry> entries3 = sftp.ls(o_sftpdtn_IRCV);

            //download all files (except the ., .. and folders) from given folder
            for (ChannelSftp.LsEntry en : entries3) {
                if (en.getFilename().equals(".") || en.getFilename().equals("..") || en.getAttrs().isDir()) {
                    continue;
                }

                String filename = StringUtils.getFilename(en.getFilename());
                //String sSubCadena = filename.substring(0, largo_archivo).toUpperCase();
                int end = filename.indexOf("_");
                String sSubCadena = filename.substring(0, end).toUpperCase();

                //LOG.info(sSubCadena);

                if (sSubCadena.equals("SDIRCVHDI")) {
                    LOG.info("Downloading IRCV " + (o_sftpdtn_IRCV + en.getFilename()) + " ---> " + pathdest + en.getFilename());
                    sftp.get(o_sftpdtn_IRCV + en.getFilename(), pathdest + en.getFilename());
                    sftp.rm(o_sftpdtn_IRCV + en.getFilename());
                    LOG.info("{} : Download Ok", dateTimeFormatter.format(LocalDateTime.now()));
                }

            }

            sftp.exit();
            channel.disconnect();
            session.disconnect();


        } catch (JSchException e) {
            LOG.error("No se pudo realizar la conexión ,{}",  e);
        } catch (SftpException e) {
            e.printStackTrace();
        }

    }
}

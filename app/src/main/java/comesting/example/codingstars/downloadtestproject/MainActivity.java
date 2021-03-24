package comesting.example.codingstars.downloadtestproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.lzyzsd.circleprogress.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button btnDownload;
    ArcProgress progressdn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnDownload=findViewById(R.id.iddownloadbtn);
        progressdn=findViewById(R.id.idprogressbardnn);

         btnDownload.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                downloadFile();

             }
         });
    }

    private void downloadFile() {
        String fileExtn = ".mp3";
        String url="https://firebasestorage.googleapis.com/v0/b/holly-quran-app-data.appspot.com/o/HOLY%20QURAN%20DATA%2FHOLY%20QURAN%20URDU%20TRANSLATION%2F002s-Baqarah.mp3?alt=media&token=351b371f-4b27-4ab4-8f68-11d7d874fb14";
        File quranFolder = new File("/HOLY QURAN App/Quran Surant");
        if (!quranFolder.exists())
        {
            quranFolder.mkdirs();
        }
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Surat downloading");
        request.setTitle("Download");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        // set DestinationInExternalFilesDir store audio in Android folders
        // and set setDestinationInExternalPublicDir is work fine in my mobile but app crash in API 30,in my frnd mobile
        request.setDestinationInExternalFilesDir(this,String.valueOf(quranFolder),"orignal file name"+fileExtn);
        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = dm.enqueue(request);
        new Thread(new Runnable() {

            @Override
            public void run() {

                boolean downloading = true;

                while (downloading) {

                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadId);

                    Cursor cursor = dm.query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false;
                    }

                    final int dl_progress = ( bytes_total > 0 ? (int) ((bytes_downloaded * 100L) / bytes_total) : 0 );

                    //final int dl_progress = (int) ((double)bytes_downloaded / (double)bytes_total * 100f);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            String file="/folderr";

                            File from=new File("/My device/Android/data/comesting.example.codingstars.downloadtestproject/files/HOLY QURAN App/Quran Surant/orignal file name.mp3");


                            progressdn.setProgress(dl_progress);
                            //when progress will be 100percent then its mean download complete, then move file from source to own directory and delete file from source
                            //i try thi code from this link
                            //https://stackoverflow.com/questions/58083140/how-to-download-file-using-downloadmanager-in-api-29-or-android-q
                            //its Z3R0 answer on stackoverflow
                            if (dl_progress==100){
                               moveFile(from,file);
                            }




                        }
                    });

                    cursor.close();
                }

            }
        }).start();



    }
    public static boolean moveFile(File source, String destPath){
        if(source.exists()){
            File dest = new File(Environment.DIRECTORY_MUSIC+destPath);
            checkMakeDirs(dest.getParent());
            try (FileInputStream fis = new FileInputStream(source);
                 FileOutputStream fos = new FileOutputStream(dest)){
                if(!dest.exists()){
                    dest.createNewFile();
                }
                writeToOutputStream(fis, fos);
                source.delete();
                return true;
            } catch (IOException ioE){
                Log.e("TAG", ioE.getMessage());
            }
        }
        return false;
    }
    public static boolean checkMakeDirs(String dirPath){
        try {
            File dir = new File(dirPath);
            return dir.exists() || dir.mkdirs();
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }
        return false;
    }
    private static void writeToOutputStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        if (is != null) {
            while ((length = is.read(buffer)) > 0x0) {
                os.write(buffer, 0x0, length);
            }
        }
        os.flush();
    }

}

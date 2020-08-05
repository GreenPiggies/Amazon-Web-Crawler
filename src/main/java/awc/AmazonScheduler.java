package awc;

import awc.csv.Entry;
import awc.jobrepo.AmazonJobRepo;
import awc.quartz.job.AmazonJob;
import awc.jobrepo.MessageQueue;
import org.quartz.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AmazonScheduler {

    public static PrintWriter writer;
    public static void main(String[] args) throws Exception {
        writer = new PrintWriter(new FileWriter(new File("amazonReviewSentimentsNew.txt")));
        writer.println("RECORD_ID,RECORD_DATETIME,RECORD_URL,RECORD_TITLE,RECORD_TEXT,DOMAIN_ROOT_URL,CITY_NAME,STATE_CODE,COUNTRY_CODE,GPS_COORDINATES,AUTHOR_ID,AUTHOR_HANDLE,AUTHOR_NAME,AUTHOR_GENDER,AUTHOR_DESCRIPTION,_AUTHOR_PROFILE_URL,AUTHOR_AVATAR_URL,AUTHOR_FOLLOWERS,AUTHOR_VERIFIED_STATUS,META_TAGS,META_TAGS2,NET_PROMOTER_SCORE,OVERALL_STAR_RATING,OVERALL_SURVEY_SCORE,SOURCE_TYPE");
        writer.flush();

        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

        Scheduler sched = schedFact.getScheduler();

        sched.start();

        int numThreads = 8;

        JobDetail[] jobs = new JobDetail[numThreads];

        AmazonJobRepo repo = AmazonJobRepo.getInstance();

        int numPagesToRead = 20;
        int page = 1;

        while (page < numPagesToRead) {
            Thread.sleep(1000);
            if (repo.hasNextLink()) {
                for (int i = 0; i < numThreads; i++) {
                    System.out.println(i);
                    if (jobs[i] == null || !repo.isRunning(i)) { // create a new job to use this
                        page++;
                        String link = repo.getNextLink();
                        System.out.println("crawler " + i + " crawling " + link);
                        JobDetail job = newJob(AmazonJob.class)
                                .withIdentity("crawler number " + i)
                                .usingJobData("url", link)
                                .usingJobData("crawler number", i)
                                .build();
                        Trigger trigger = newTrigger()
                                .startNow()
                                .build();
                        sched.scheduleJob(job, trigger);
                        jobs[i] = job;
                        break;
                    }
                }
            }
        }

        sched.shutdown(true);

        System.out.println("done");

        for (Entry e : repo.getData()) {
            writer.println(e);
        }
        writer.close();


    }
}
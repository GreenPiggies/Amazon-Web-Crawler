package wc.quartz.scheduler;

import wc.csv.Entry;
import wc.jobrepo.AmazonJobRepo;
import wc.quartz.job.AmazonJob;
import org.quartz.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class AmazonScheduler {

    public static void main(String[] args) throws Exception {
        // Instantiate the writer for our CSV data file
        PrintWriter writer = new PrintWriter(new FileWriter(new File("amazonReviewSentimentsNew.txt")));
        writer.println("RECORD_ID,RECORD_DATETIME,RECORD_URL,RECORD_TITLE,RECORD_TEXT,DOMAIN_ROOT_URL,CITY_NAME,STATE_CODE,COUNTRY_CODE,GPS_COORDINATES,AUTHOR_ID,AUTHOR_HANDLE,AUTHOR_NAME,AUTHOR_GENDER,AUTHOR_DESCRIPTION,_AUTHOR_PROFILE_URL,AUTHOR_AVATAR_URL,AUTHOR_FOLLOWERS,AUTHOR_VERIFIED_STATUS,META_TAGS,META_TAGS2,NET_PROMOTER_SCORE,OVERALL_STAR_RATING,OVERALL_SURVEY_SCORE,SOURCE_TYPE");
        writer.flush();


        // Create the scheduler
        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
        Scheduler sched = schedFact.getScheduler();
        sched.start();

        // instantiate the threads and the job repo
        int numThreads = 8;
        int numPagesToRead = 20;
        JobDetail[] jobs = new JobDetail[numThreads];
        AmazonJobRepo repo = AmazonJobRepo.getInstance();
        int page = 1;

        // start crawling
        while (page < numPagesToRead) {
            // politeness delay
            Thread.sleep(1000);

            // check for a new link
            if (repo.hasNextLink()) {

                // iterate through all threads, assign a crawling task to the first available thread
                for (int i = 0; i < numThreads; i++) {
                    if (jobs[i] == null || !repo.isRunning(i)) { // create a new job to use this
                        page++;
                        String link = repo.getNextLink();
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

        for (Entry e : repo.getData()) {
            writer.println(e);
        }
        writer.close();


    }
}
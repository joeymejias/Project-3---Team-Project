package com.joeymejias.chewsit;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

/**
 * Created by joshuagoldberg on 8/17/16.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RecommendJobService extends JobService {

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        if (YelpHelper.getInstance().getRecommendedBusiness() != null) {
            Intent intent = new Intent(this, DetailActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 666, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.local_dining_icon);
            builder.setContentTitle("Check out " + YelpHelper.getInstance().getRecommendedBusiness().name());
            builder.setContentText("It's highly rated! (" + YelpHelper.getInstance().getRecommendedBusiness().rating() +
                    " stars)");
            builder.setPriority(Notification.PRIORITY_DEFAULT);
            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);
            NotificationManagerCompat.from(this).notify(123, builder.build());
        }
        jobFinished(jobParameters, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}

package com.cubetrek.viewer;

import com.cubetrek.database.TrackData;
import com.cubetrek.database.TrackDataRepository;
import com.cubetrek.database.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class ActivitityService {

    @Autowired
    TrackDataRepository trackDataRepository;

    public String getActivityHeatmapAsJSON(Users user, TimeZone timeZone) {
        return trackDataRepository.getAggregatedStatsAsJSON(user.getId(), timeZone.getID());
    }

    public List<ActivityCount> getActivityTypeCount(Users user) {
        //convert List of ActivityCountInterface to list of ActivityCount
        return trackDataRepository.getActivityCounts(user.getId()).stream()
                .map(ActivityCount::new)
                .collect(Collectors.toList());
    }

    public static class ActivityCount {
        public TrackData.Activitytype activitytype;
        public int count;
        public ActivityCount(TrackDataRepository.ActivityCountInterface act) {
            activitytype = TrackData.Activitytype.values()[act.getActivitytype()];
            count = act.getCount();
        }
    }

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd. MMMM yyyy HH:mm");
    public TopActivities getTopActivities(Users user) {
        TopActivities out = new TopActivities();
        out.recentDistance = trackDataRepository.findTopDistanceLast3Month(user.getId(), 5);
        out.alltimeDistance = trackDataRepository.findTopDistanceAlltime(user.getId(), 5);
        out.recentAscent = trackDataRepository.findTopAscentLast3Month(user.getId(), 5);
        out.alltimeAscent = trackDataRepository.findTopAscentAlltime(user.getId(), 5);
        out.recentPeak = trackDataRepository.findTopPeakLast3Month(user.getId(), 5);
        out.alltimePeak = trackDataRepository.findTopPeakAlltime(user.getId(), 5);
        return out;
    }

    public static class TopActivities {
        public List<TrackData.TrackMetadata> recentDistance;
        public List<TrackData.TrackMetadata> alltimeDistance;
        public List<TrackData.TrackMetadata> recentAscent;
        public List<TrackData.TrackMetadata> alltimeAscent;
        public List<TrackData.TrackMetadata> recentPeak;
        public List<TrackData.TrackMetadata> alltimePeak;
    }


    public Page<TrackData.TrackMetadata> getTenRecentActivities(Users user, Integer pageNo) {
        PageRequest paging = PageRequest.of(pageNo, 10, Sort.by("datetrack").descending());
        return trackDataRepository.findByOwnerAndHidden(user, false, paging);
    }


    public List<TrackData.TrackMetadata> getActivitiesList(Users user, Integer size, Integer pageNo, String sort, boolean descending, TrackData.Activitytype activitytype) {
        Sort sorter;
        sorter = descending ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        PageRequest paging = PageRequest.of(pageNo, size, sorter);
        if (activitytype == null)
            return trackDataRepository.findByOwnerAndHidden(user, false, paging).stream().toList();
        else
            return trackDataRepository.findByOwnerAndHiddenAndActivitytype(user, false, activitytype, paging);
    }

    public long countNumberOfEntries(Users user) {
        return trackDataRepository.countByOwnerAndHidden(user, false);
    }


}
// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;
import java.util.TreeSet;


public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<TimeRange> timeRange = new ArrayList<>();
    if (request.getAttendees().isEmpty()) {
        timeRange.add(TimeRange.WHOLE_DAY);
        return timeRange;
    }
    else if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
        return timeRange;
    }
    else {
        // select all events relevant to attendees
        Collection<String> attendees = request.getAttendees();
        ArrayList<Event> relevantEvent = new ArrayList<>();
        for (Iterator<Event> iter = events.iterator(); iter.hasNext();) {
            Event thisEvent = iter.next();
            Collection<String> tmp_attendees = thisEvent.getAttendees();
            if (has_intersection(tmp_attendees, attendees)) {
                relevantEvent.add(thisEvent);
            }
        }

        //merge these events
        ArrayList<TimeRange> relEventsTime =  new ArrayList<>();
        for (Iterator<Event> iter = relevantEvent.iterator(); iter.hasNext();) {
            Event tmpEvent = iter.next();
            relEventsTime.add(TimeRange.fromStartDuration(tmpEvent.getWhen().start(), tmpEvent.getWhen().duration()));
        }
        relEventsTime.sort(TimeRange.ORDER_BY_START);
        Iterator<TimeRange> iter = relEventsTime.iterator();
        ArrayList<TimeRange> mergedTimeRange = new ArrayList<>();
        TimeRange curTimeRange;
        if (!iter.hasNext()) {
            timeRange.add(TimeRange.WHOLE_DAY);
            return timeRange;
        }
        curTimeRange = iter.next();
        boolean flag = false;

        while (iter.hasNext()) {
            TimeRange tmpTimeRange = iter.next();
            if (curTimeRange.overlaps(tmpTimeRange) || tmpTimeRange.overlaps(curTimeRange)) {
                flag = true;
                int endtime = Math.max(curTimeRange.end(), tmpTimeRange.end());
                //System.out.println(endtime);
                curTimeRange = TimeRange.fromStartEnd(curTimeRange.start(), endtime, false);
                mergedTimeRange.add(curTimeRange);
            }
            else {
                flag = false;
                mergedTimeRange.add(curTimeRange);
                curTimeRange = tmpTimeRange;
            }
        }
        if (!flag) {
            mergedTimeRange.add(curTimeRange);
        }

        // find free TimeRanges
        iter = mergedTimeRange.iterator();
        int currentTime = TimeRange.START_OF_DAY;
        while (iter.hasNext()) {
            TimeRange tmpTimeRange = iter.next();
            if (tmpTimeRange.start()-currentTime >= request.getDuration()) {
                timeRange.add(TimeRange.fromStartEnd(currentTime, tmpTimeRange.start(), false));
            }
            currentTime = tmpTimeRange.end();
        }
        if (currentTime < TimeRange.END_OF_DAY) timeRange.add(TimeRange.fromStartEnd(currentTime, TimeRange.END_OF_DAY, true));

        return timeRange;
    }
  }
  private boolean has_intersection(Collection<String> a, Collection<String> b) {
      Iterator itera = a.iterator();
      Iterator iterb = b.iterator();
      while (itera.hasNext()) {
          String x = (String)itera.next();
          while (iterb.hasNext()) {
              if (x.equals(iterb.next())) {
                  return true;
              }
          }
      }
      return false;
  }
}

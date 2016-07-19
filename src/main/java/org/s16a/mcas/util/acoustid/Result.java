package org.s16a.mcas.util.acoustid;

import java.util.ArrayList;
import java.util.List;

class Result {
   String id;
   List<Recording> recordings = new ArrayList<Recording>();
   String score;
}
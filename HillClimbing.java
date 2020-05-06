package timetablingfinal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Scanner;

/**
 *
 * @author user
 */
public class HillClimbing {

    public static void main(String[] args) throws IOException {
        String nama[][] =           {
                                    {"car-f-92", "CAR92"}, 
                                    {"car-s-91", "CAR91"}, 
                                    {"ear-f-83", "EAR83"}, 
                                    {"hec-s-92", "HEC92"},
                                    {"kfu-s-93", "KFU93"}, 
                                    {"lse-f-91", "LSE91"}, 
                                    {"pur-s-93", "PUR93"}, 
                                    {"rye-s-93", "RYE93"}, 
                                    {"sta-f-83", "STA83"},
                                    {"tre-s-92", "TRE92"},	
                                    {"uta-s-92", "UTA92"}, 
                                    {"ute-s-92", "UTE92"}, 
                                    {"yor-f-83", "YOR83"}};
        for (int i = 0; i < nama.length; i++) {
            System.out.println(i + 1 + "  " + nama[i][0]);
        }
        Scanner in = new Scanner(System.in);
        System.out.print("Nomor dari exam yang akan dijadwalankan : ");
        int pilihan = in.nextInt();
        String ex = "";
        String out = "";
        for (int i = 0; i < nama.length; i++) {
            if (pilihan == i + 1) {
                ex = nama[i][0];
                out = nama[i][1];
            }
        }
        final String path = "D:/Kuliah PAHMI/Semester 6/OKH/FP/Toronto/" + ex;
        
        BufferedReader reader = new BufferedReader(new FileReader(path + ".crs"));
        int exam = 0;
        while (reader.readLine() != null) {
            exam++;
        }
        reader.close();
        
        BufferedReader baca = new BufferedReader(new FileReader(path + ".stu"));
        int student = 0;
        while (baca.readLine() != null) {
            student++;
        }
        reader.close();
        
        int data[][] = new int[exam][exam];
        int sort[][] = new int[exam][2];
        int timeslot[][] = new int[exam][2];
        int ts = 1;
        int count = 0;
        double deltapenalty = 0;
        int max[][] = new int[1][2];
        max[0][0] = -1;
        max[0][1] = -1;
        int x = 0;
        
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                data[i][j] = 0;
            }
        }
        
        int degree[][] = new int[exam][2];
        for (int i = 0; i < degree.length; i++) {
            for (int j = 0; j < degree[0].length; j++) {
                degree[i][0] = i + 1;
            }
        }
        
        long mulai = System.nanoTime();
        try {
            File f = new File(path + ".stu");

            BufferedReader b = new BufferedReader(new FileReader(f));

            String readLine = "";

            while ((readLine = b.readLine()) != null) {
                //System.out.println(readLine);
                String tmp[] = readLine.split(" ");
                for (int i = 0; i < tmp.length; i++) {
                    for (int j = 0; j < tmp.length; j++) {
                        if (tmp[i] != tmp[j]) {
                            data[Integer.parseInt(tmp[i]) - 1][Integer.parseInt(tmp[j]) - 1]++;
                        }
                    }
                }
            }
            for (int i = 0; i < exam; i++) {
                for (int j = 0; j < exam; j++) {
                    if (data[i][j] > 0) {
                        count++;
                    } else {
                        count = count;
                    }
                }
                degree[i][1] = count;
                count = 0;
            }

            for (int a = 0; a < degree.length; a++) {
                for (int i = 0; i < degree.length; i++) {
                    if (max[0][1] < degree[i][1]) {
                        max[0][0] = degree[i][0];
                        max[0][1] = degree[i][1];
                        x = i;
                    }
                }
                degree[x][0] = -2;
                degree[x][1] = -2;
                sort[a][0] = max[0][0];
                sort[a][1] = max[0][1];
                max[0][0] = -1;
                max[0][1] = -1;
            }
            for (int i = 0; i < timeslot.length; i++) {
                for (int j = 0; j < timeslot[i].length; j++) {
                    timeslot[i][0] = i + 1;
                    timeslot[i][1] = -1;
                }
            }
            for (int i = 0; i < sort.length; i++) {
                for (int j = 0; j < ts; j++) {
                    if (isTimeslotAvailableWithSorted(i, j, data, sort, timeslot)) {
                        timeslot[sort[i][0] - 1][1] = j;
                        break;
                    } else {
                        ts++;
                    }
                }
            }

            for (int i = 0; i < timeslot.length; i++) //print timeslot
            {
                for (int j = 0; j < timeslot[i].length; j++) {
                    System.out.print(timeslot[i][j] + " ");
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("Penalty awal: " + getPenalty(timeslot, data, student));

        int tt = 0;
        for (int i = 0; i < timeslot.length; i++) {
            if (timeslot[i][1] > tt) {
                tt = timeslot[i][1];
            }
        }
        tt = tt + 1;
        hillClimbing(timeslot, data, student, tt, exam, deltapenalty);
        long selesai = System.nanoTime();
        long runningtime = selesai - mulai;
        System.out.println("Running time: " + (double) runningtime / 1000000000);
    }

    public static double getPenalty(int[][] schedule, int[][] matrix, int student) {
        double penalty = 0;

        for (int i = 0; i < matrix.length - 1; i++) {
            for (int j = i + 1; j < matrix.length; j++) {
                if (matrix[i][j] != 0) {
                    if (Math.abs(schedule[j][1] - schedule[i][1]) >= 1 && Math.abs(schedule[j][1] - schedule[i][1]) <= 5) {
                        penalty = penalty + (matrix[i][j] * (Math.pow(2, 5 - (Math.abs(schedule[j][1] - schedule[i][1])))));
                    }
                }
            }
        }

        return penalty / student;
    }

    public static boolean isTimeslotAvailableWithSorted(int course, int timeslot, int conflictmatrix[][], int sortedmatrix[][], int timeslotarray[][]) {
        for (int i = 0; i < sortedmatrix.length; i++) {
            if (conflictmatrix[sortedmatrix[course][0] - 1][i] != 0 && timeslotarray[i][1] == timeslot) {
                return false;
            }
        }

        return true;
    }

    public static boolean isTimeslotAvailable(int course, int timeslot, int conflictmatrix[][], int timeslotarray[][]) {
        for (int i = 0; i < conflictmatrix.length; i++) {
            if (conflictmatrix[course][i] != 0 && timeslotarray[i][1] == timeslot) {
                return false;
            }
        }

        return true;
    }

    public static void hillClimbing(int jadwal[][], int matrix[][], int student, int timeslotnew, int coursenew, double deltapenalty) {
        int waktu[][] = new int[jadwal.length][2];
        int waktu2[][] = new int[jadwal.length][2];
        for (int i = 0; i < jadwal.length; i++) {
            for (int j = 0; j < jadwal[i].length; j++) {
                waktu[i][j] = jadwal[i][j];
                waktu2[i][j] = jadwal[i][j];
            }
        }
        double s = getPenalty(waktu, matrix, student);
        double d = s;
        for (int i = 0; i < 1000000; i++) {
            int exr = (int) (Math.random() * (coursenew - 1));
            int ttr = (int) (Math.random() * (timeslotnew - 1));
            if (isTimeslotAvailable(exr, ttr, matrix, waktu2)) {
                waktu2[exr][1] = ttr;
                double c = getPenalty(waktu2, matrix, student);
                if (c < s) {
                    s = getPenalty(waktu2, matrix, student);
                    waktu[exr][1] = waktu2[exr][1];
                } else {
                    waktu2[exr][1] = waktu[exr][1];
                }
            }

            deltapenalty = ((d - s) / d) * 100;
            System.out.println("/br");
            System.out.println("Iterasi ke " + (i + 1) + " penaltinya: " + getPenalty(waktu2, matrix, student));

        }
        System.out.println("\n" + "----------------------------------Hasil Algorita Hill Climbing----------------------------------------");
        System.out.println("\nPenalty Initial : " + d);
        System.out.println("\nPenalty terbaik : " + s);
        System.out.println("\nDelta Penalti : " + deltapenalty + " % " + "\n");

    }
}

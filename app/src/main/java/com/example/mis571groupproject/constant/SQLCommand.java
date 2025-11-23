package com.example.mis571groupproject.constant;
/**
 * SQL commands
 * Including select/delete/update/insert
 */
public abstract class SQLCommand
{
    //query all students
    public static String QUERY_STUDENT = "select stid, stname from Student";

    //list all data in books table
    public static String QUERY_1 = "select lbcallnum, lbtitle from LibBook";

    //List the call numbers of books with the title ‘Database Management’
    public static String QUERY_2 = "select lbcallnum from LibBook where lbtitle like '%Database Management%'";

    //List all students and the books they have checked out
    public static String QUERY_3 =
            "select s.stname, l.lbtitle from student s " +
                    "join checkout c on s.stid = c.stid " +
                    "join libbook l on c.lbcallnum = l.lbcallnum";

    //List the titles of books currently checked out
    public static String QUERY_4 =
            "select l.lbtitle from libbook l " +
                    "join checkout c on l.lbcallnum = c.lbcallnum " +
                    "where c.coreturned = 'N'";

    //List the total fine owed by each student
    public static String QUERY_5 =
            "select s.stname, sum(c.cofine) as total_fine from student s " +
                    "join checkout c on s.stid = c.stid " +
                    "group by s.stname";

    //List students who have no fines
    public static String QUERY_6 =
            "select s.stname from student s " +
                    "where s.stid not in (select stid from checkout where cofine > 0)";

    //List books that have been returned
    public static String QUERY_7 =
            "select l.lbtitle from libbook l " +
                    "join checkout c on l.lbcallnum = c.lbcallnum " +
                    "where c.coreturned = 'Y'";

    //List students who have checked out at least one book
    public static String QUERY_8 =
            "select distinct s.stname from student s " +
                    "join checkout c on s.stid = c.stid";

}

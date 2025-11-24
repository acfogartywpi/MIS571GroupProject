package com.example.mis571groupproject.constant;
/**
 * SQL commands
 * Including select/delete/update/insert
 */
public abstract class SQLCommand
{
    public static String QUERY_1 =
            "SELECT oi.seller_id, ROUND(AVG(r.review_score), 2) AS avg_review_score FROM Order_Item oi " +
                    "JOIN Order_Review r ON oi.order_id = r.order_id " +
                    "GROUP BY oi.seller_id "+
                    "ORDER BY avg_review_score DESC";

    public static String QUERY_2 =
            "WITH monthly_revenue AS ( " +
                    "    SELECT " +
                    "        strftime('%m', o.Order_purchase_timestamp) AS month_num, " +
                    "        CASE strftime('%m', o.Order_purchase_timestamp) " +
                    "            WHEN '01' THEN 'January' " +
                    "            WHEN '02' THEN 'February' " +
                    "            WHEN '03' THEN 'March' " +
                    "            WHEN '04' THEN 'April' " +
                    "            WHEN '05' THEN 'May' " +
                    "            WHEN '06' THEN 'June' " +
                    "            WHEN '07' THEN 'July' " +
                    "            WHEN '08' THEN 'August' " +
                    "            WHEN '09' THEN 'September' " +
                    "            WHEN '10' THEN 'October' " +
                    "            WHEN '11' THEN 'November' " +
                    "            WHEN '12' THEN 'December' " +
                    "        END AS month_name, " +
                    "        pt.Product_category_name_english AS category, " +
                    "        SUM(oi.price) AS total_sales " +
                    "    FROM Order_Item oi " +
                    "    JOIN Orders o ON oi.Order_id = o.Order_id " +
                    "    JOIN Product p ON oi.Product_id = p.Product_id " +
                    "    JOIN Product_Translation pt ON p.Product_category_name = pt.Product_category_name " +
                    "    GROUP BY month_num, category " +
                    "), " +
                    "ranked AS ( " +
                    "    SELECT " +
                    "        month_num, " +
                    "        month_name, " +
                    "        category, " +
                    "        total_sales, " +
                    "        ROW_NUMBER() OVER ( " +
                    "            PARTITION BY month_num " +
                    "            ORDER BY total_sales DESC " +
                    "        ) AS rn " +
                    "    FROM monthly_revenue " +
                    ") " +
                    "SELECT month_name AS month, category, total_sales " +
                    "FROM ranked " +
                    "WHERE rn IN (1, 2) " +
                    "ORDER BY month_num, rn;";

    public static String QUERY_3 =
            "WITH state_sales AS ( " +
                    "    SELECT " +
                    "        c.Customer_state AS state, " +
                    "        pt.Product_category_name_english AS category, " +
                    "        SUM(oi.price) AS total_sales " +
                    "    FROM Order_Item oi " +
                    "    JOIN Orders o ON oi.Order_id = o.Order_id " +
                    "    JOIN Customer c ON o.Customer_unique_id = c.Customer_unique_id " +
                    "    JOIN Product p ON oi.Product_id = p.Product_id " +
                    "    JOIN Product_Translation pt ON p.Product_category_name = pt.Product_category_name " +
                    "    GROUP BY state, category " +
                    "), " +
                    "ranked AS ( " +
                    "    SELECT " +
                    "        state, " +
                    "        category, " +
                    "        total_sales, " +
                    "        ROW_NUMBER() OVER ( " +
                    "            PARTITION BY state " +
                    "            ORDER BY total_sales DESC " +
                    "        ) AS best_rank, " +
                    "        ROW_NUMBER() OVER ( " +
                    "            PARTITION BY state " +
                    "            ORDER BY total_sales ASC " +
                    "        ) AS worst_rank " +
                    "    FROM state_sales " +
                    ") " +
                    "SELECT state, category, total_sales " +
                    "FROM ranked " +
                    "WHERE best_rank = 1 OR worst_rank = 1 " +
                    "ORDER BY state, CASE WHEN best_rank = 1 THEN 1 ELSE 2 END;";

    public static String QUERY_4 =
            "WITH delivery_times AS ( " +
                    "    SELECT " +
                    "        oi.Seller_id, " +
                    "        JULIANDAY(o.Order_delivered_customer_date) - JULIANDAY(o.Order_purchase_timestamp) AS delivery_days " +
                    "    FROM Order_Item oi " +
                    "    JOIN Orders o ON oi.Order_id = o.Order_id " +
                    "    WHERE o.Order_delivered_customer_date IS NOT NULL " +
                    "      AND o.Order_purchase_timestamp IS NOT NULL " +
                    ") " +
                    "SELECT " +
                    "    Seller_id, " +
                    "    ROUND(AVG(delivery_days), 2) AS average_delivery_time " +
                    "FROM delivery_times " +
                    "GROUP BY Seller_id " +
                    "ORDER BY average_delivery_time DESC;";

    public static String QUERY_5 =
            "SELECT " +
                    "    c.Customer_state AS state, " +
                    "    SUM(oi.price) AS total_sales, " +
                    "    COUNT(DISTINCT o.Order_id) AS total_orders " +
                    "FROM Customer c " +
                    "JOIN Orders o ON c.Customer_unique_id = o.Customer_unique_id " +
                    "JOIN Order_Item oi ON o.Order_id = oi.Order_id " +
                    "GROUP BY c.Customer_state " +
                    "ORDER BY total_sales DESC;";

    public static String QUERY_6 =
            "WITH delivery_data AS ( " +
                    "    SELECT " +
                    "        r.review_score, " +
                    "        JULIANDAY(o.Order_delivered_customer_date) - JULIANDAY(o.Order_purchase_timestamp) AS delivery_days " +
                    "    FROM Orders o " +
                    "    JOIN Order_Review r ON o.Order_id = r.Order_id " +
                    "    WHERE o.Order_delivered_customer_date IS NOT NULL " +
                    "      AND o.Order_purchase_timestamp IS NOT NULL " +
                    "), " +
                    "grouped AS ( " +
                    "    SELECT " +
                    "        review_score, " +
                    "        delivery_days, " +
                    "        CASE " +
                    "            WHEN delivery_days <= 3 THEN 'Fast (0–3 days)' " +
                    "            WHEN delivery_days <= 7 THEN 'Medium (4–7 days)' " +
                    "            WHEN delivery_days <= 14 THEN 'Slow (8–14 days)' " +
                    "            ELSE 'Very Slow (15+ days)' " +
                    "        END AS delivery_speed_group " +
                    "    FROM delivery_data " +
                    ") " +
                    "SELECT " +
                    "    delivery_speed_group, " +
                    "    ROUND(AVG(delivery_days), 2) AS avg_delivery_time, " +
                    "    ROUND(AVG(review_score), 2) AS avg_review_score " +
                    "FROM grouped " +
                    "GROUP BY delivery_speed_group " +
                    "ORDER BY avg_delivery_time ASC;";

    public static String QUERY_7 =
            "SELECT " +
                    "    o.Customer_unique_id, " +
                    "    COUNT(o.Order_id) AS total_orders " +
                    "FROM Orders o " +
                    "GROUP BY o.Customer_unique_id " +
                    "HAVING COUNT(o.Order_id) > 1 " +
                    "ORDER BY total_orders DESC;";

    public static String QUERY_8 =
            "WITH category_orders AS ( " +
                    "    SELECT " +
                    "        pt.Product_category_name_english AS category, " +
                    "        o.Order_id, " +
                    "        SUM(oi.price) AS order_value " +
                    "    FROM Order_Item oi " +
                    "    JOIN Orders o ON oi.Order_id = o.Order_id " +
                    "    JOIN Product p ON oi.Product_id = p.Product_id " +
                    "    JOIN Product_Translation pt ON p.Product_category_name = pt.Product_category_name " +
                    "    GROUP BY pt.Product_category_name_english, o.Order_id " +
                    ") " +
                    "SELECT " +
                    "    category, " +
                    "    ROUND(AVG(order_value), 2) AS average_order_value " +
                    "FROM category_orders " +
                    "GROUP BY category " +
                    "ORDER BY average_order_value DESC;";

    public static String QUERY_9 =
            "SELECT " +
                    "    pt.Product_category_name_english AS category, " +
                    "    ROUND(AVG(r.review_score), 2) AS average_review_score " +
                    "FROM Order_Item oi " +
                    "JOIN Orders o ON oi.Order_id = o.Order_id " +
                    "JOIN Order_Review r ON o.Order_id = r.Order_id " +
                    "JOIN Product p ON oi.Product_id = p.Product_id " +
                    "JOIN Product_Translation pt ON p.Product_category_name = pt.Product_category_name " +
                    "GROUP BY pt.Product_category_name_english " +
                    "ORDER BY average_review_score DESC;";


    public static String QUERY_10 =
            "SELECT " +
                    "    oi.Seller_id, " +
                    "    SUM(oi.price) AS total_revenue " +
                    "FROM Order_Item oi " +
                    "GROUP BY oi.Seller_id " +
                    "ORDER BY total_revenue DESC;";

    public static String QUERY_11 =
            "SELECT " +
                    "    c.Customer_state AS state, " +
                    "    ROUND(AVG(JULIANDAY(o.Order_delivered_customer_date) - JULIANDAY(o.Order_purchase_timestamp)), 2) AS average_delivery_time " +
                    "FROM Orders o " +
                    "JOIN Customer c ON o.Customer_unique_id = c.Customer_unique_id " +
                    "WHERE o.Order_delivered_customer_date IS NOT NULL " +
                    "  AND o.Order_purchase_timestamp IS NOT NULL " +
                    "GROUP BY c.Customer_state " +
                    "ORDER BY average_delivery_time DESC;";

    public static String QUERY_12 =
            "WITH hourly AS ( " +
                    "    SELECT CAST(strftime('%H', Order_purchase_timestamp) AS INTEGER) AS hour_24 " +
                    "    FROM Orders " +
                    "    WHERE Order_purchase_timestamp IS NOT NULL " +
                    "), " +
                    "formatted AS ( " +
                    "    SELECT hour_24, " +
                    "        CASE " +
                    "            WHEN hour_24 = 0 THEN '12–1 AM' " +
                    "            WHEN hour_24 = 1 THEN '1–2 AM' " +
                    "            WHEN hour_24 = 2 THEN '2–3 AM' " +
                    "            WHEN hour_24 = 3 THEN '3–4 AM' " +
                    "            WHEN hour_24 = 4 THEN '4–5 AM' " +
                    "            WHEN hour_24 = 5 THEN '5–6 AM' " +
                    "            WHEN hour_24 = 6 THEN '6–7 AM' " +
                    "            WHEN hour_24 = 7 THEN '7–8 AM' " +
                    "            WHEN hour_24 = 8 THEN '8–9 AM' " +
                    "            WHEN hour_24 = 9 THEN '9–10 AM' " +
                    "            WHEN hour_24 = 10 THEN '10–11 AM' " +
                    "            WHEN hour_24 = 11 THEN '11–12 PM' " +
                    "            WHEN hour_24 = 12 THEN '12–1 PM' " +
                    "            WHEN hour_24 = 13 THEN '1–2 PM' " +
                    "            WHEN hour_24 = 14 THEN '2–3 PM' " +
                    "            WHEN hour_24 = 15 THEN '3–4 PM' " +
                    "            WHEN hour_24 = 16 THEN '4–5 PM' " +
                    "            WHEN hour_24 = 17 THEN '5–6 PM' " +
                    "            WHEN hour_24 = 18 THEN '6–7 PM' " +
                    "            WHEN hour_24 = 19 THEN '7–8 PM' " +
                    "            WHEN hour_24 = 20 THEN '8–9 PM' " +
                    "            WHEN hour_24 = 21 THEN '9–10 PM' " +
                    "            WHEN hour_24 = 22 THEN '10–11 PM' " +
                    "            WHEN hour_24 = 23 THEN '11–12 AM' " +
                    "        END AS time_of_day " +
                    "    FROM hourly " +
                    ") " +
                    "SELECT time_of_day, COUNT(*) AS total_orders " +
                    "FROM formatted " +
                    "GROUP BY time_of_day, hour_24 " +
                    "ORDER BY total_orders DESC;";

    public static String QUERY_13 =
            "SELECT Customer_state AS state, " +
                    "       COUNT(DISTINCT Customer_unique_id) AS total_customers " +
                    "FROM Customer " +
                    "GROUP BY Customer_state " +
                    "ORDER BY total_customers DESC;";


}

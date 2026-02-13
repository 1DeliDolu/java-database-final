#GlobalExceptionHandler.java
package com.project.code.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleJsonParseException(HttpMessageNotReadableException ex) {
        Map<String,Object> map=new HashMap<>();
        map.put("message","Invalid input: The data provided is not valid.");
        return map;
    }
}

Solution Stored Procedures
Monthly sales by store 

DELIMITER $$
CREATE PROCEDURE GetMonthlySalesForEachStore(IN year_param INT, IN month_param INT)
BEGIN
    SELECT 
        od.store_id, 
        SUM(DISTINCT od.total_price) AS total_sales,  -- Use DISTINCT to avoid duplicates
        MONTH(od.date) AS sale_month,
        YEAR(od.date) AS sale_year
    FROM 
        order_details od
    JOIN 
        order_item oi ON od.id = oi.order_id
    WHERE 
        YEAR(od.date) = year_param
        AND MONTH(od.date) = month_param
    GROUP BY 
        od.store_id, MONTH(od.date), YEAR(od.date)
    ORDER BY 
        total_sales DESC;
END $$
DELIMITER ;

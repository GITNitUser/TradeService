# Trade Enrichment Service

This service enriches trade data with product names and performs data validation.

## How to Run

1. Clone the repository.
2. Navigate to the project directory.
3. Run the following command:

   ```bash
   mvn spring-boot:run

### Calling the Enrich Trade API
To enrich trade data, you can call the below api:

###### POST: 
```
http://localhost:8080/api/v1/enrich
```
######  Request Headers:
```
Content-Type: multipart/form-data
Accept: text/csv
```
######  Request Body: 
    file: @trade.csv
#### Sample trade data file: trade.csv
```
date,product_id,currency,price
20160101,1,EUR,10.0
20160101,2,EUR,20.1
20160101,3,EUR,30.34
20160101,11,EUR,35.34
```
#### Sample HTTP Response: (text/csv)
```
date,product_name,currency,price
20160101,Treasury Bills Domestic,EUR,10.0
20160101,Corporate Bonds Domestic,EUR,20.1
20160101,REPO Domestic,EUR,30.34
20160101,Missing Product Name,EUR,35.34
```

### Ideas Implemented
1. Product data is loaded on request to save memory, particularly for large product files.
2. Trade.csv files of any size can be uploaded.
3. Parallel streams are utilized for enriching trade data and loading product data from CSV files, enhancing performance, especially for large files.
4. Validation is added for CSV file extension and empty trade file requests.
5. Comprehensive test cases ensure 100% code coverage.
6. A generic approach is employed for loading the product.csv file, accommodating future changes.
7. Proper error handling and logging mechanisms are implemented.

### Limitations
1. Only the CSV file type is supported.

### Feedback
We welcome your feedback and suggestions for improvement! Please share your thoughts with us by emailing 
[ilyas.abbasi@dxc.com](mailto:ilyas.abbasi@dxc.com)
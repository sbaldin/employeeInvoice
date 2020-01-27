#  Kotlin Invoicer Utility
Kotlin console utility to generate the employee's payment invoice. 
Can generate invoices as PDF or XLSX files for local and foreign banking operations.  

## Getting Started

You can run following gradle command to generate signed PDF invoices with default params:
```
 ./gradlew runShadow
```
Also you can provide your own params by specifing path to config file:
```
 ./gradlew runShadow -Pjargs="path/to/application.yaml"
```
or you have assembled jar:
```
java -jar invoicer.jar path/to/application.yaml
```
### Installing
To install assembled fat jar and default application.yaml to hidden folder `.invoicer` in the user home directory use following command:
```
 ./gradlew installToUserHomeDir
```

## Customization

`Application.yaml` holds all properties to customization invoices.

The`app` section allow you to specify what kind of invoice you want to generate(for local or foreign bank or both), setup generated file type(xlsx + word or couple pdf) and provide output path. 

The `employee` section allows you to specify 'for whom' invoices will be generated.

The `banking` section allows you to specify bank details for both types of invoices.
```
app:
  appRunType: BOTH #FOREIGN_BANK_INVOICE #LOCAL_BANK_INVOICE #BOTH
  resultFileType: PDF #OFFICE #PDF
  outputPath: / #relative path from user home directory
employee:
  name: Ivan Ivanov
  invoiceDate: 2000-01-01
  contractDate: 1970-01-01
  serviceProvider: Platform Development
  vacationDaysInMonth: 1
  vacationDaysInYear: 2
  monthRate: 1000
  additionalExpenses: 100
  signPath: # build/resources/main/sign.png # path to your signature, leave empty to skip signature adding
banking:
  local:
    name: AO «BANK»
    accountNumber: 0000 0000 0000 0000 0001
    country: Russia
    address: 27 Lenina str., Moscow, 107078, tel +7 495 755-58-58, SWIFT BANKRUM
    beneficiaryName: IP Ivanov Ivan Ivanovich
    beneficiaryAddress: PR. LENINA, D. 1, KV. 1, KEMEROVO, RUSSIA, 650000
  foreign:
    name: BANK
    accountNumber: 000000101
    contractorName: BigCompany LLC
    address: 10 10th Street NE Atlanta, GA 30309 United States
```

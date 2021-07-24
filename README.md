# Quotes App

[![CI](https://github.com/ilya-murzinov/quotes/actions/workflows/ci.yml/badge.svg)](https://github.com/ilya-murzinov/quotes/actions/workflows/ci.yml)

## Run the app:
```shell
./run.sh
```

## API

```shell
curl -vvv localhost:8081/instruments                                                                                            
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8081 (#0)
> GET /instruments HTTP/1.1
> Host: localhost:8081
> User-Agent: curl/7.64.1
> Accept: */*
>
< HTTP/1.1 200 OK
< content-type: application/json
< content-length: 7684
<
* Connection #0 to host localhost left intact
[
  {
    "isin": "AA660TK25875",
    "description": "reformidans errem maluisset",
    "price": 602.9158
  },
  {
    "isin": "AH4586401751",
    "description": "rhoncus quaestio varius perpetua debet efficitur",
    "price": null
  }
]

curl -vvv localhost:8081/price-history/AA660TK25875                                                                                             
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8081 (#0)
> GET /price-history/AA660TK25875 HTTP/1.1
> Host: localhost:8081
> User-Agent: curl/7.64.1
> Accept: */*
>
< HTTP/1.1 200 OK
< content-type: application/json
< content-length: 204
<
* Connection #0 to host localhost left intact
{
  "isin": "AA660TK25875",
  "description": "reformidans errem maluisset",
  "priceHistory": [
    {
      "start": 1627142580000,
      "end": 1627142640000,
      "openPrice": 991.3459,
      "closePrice": 1004.4212,
      "high": 1009.0753,
      "low": 981.7295
    }
  ]
}


curl -vvv localhost:8081/hot-instruments
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8081 (#0)
> GET /hot-instruments HTTP/1.1
> Host: localhost:8081
> User-Agent: curl/7.64.1
> Accept: */*
>
< HTTP/1.1 200 OK
< content-type: application/json
< content-length: 2373
<
* Connection #0 to host localhost left intact
[
  {
    "isin": "SB7L1777S456",
    "description": "explicari ocurreret mentitum mediocritatem",
    "priceChangePercent": 812.86
  },
  {
    "isin": "DD6070L50582",
    "description": "venenatis quo quaerendum utinam",
    "priceChangePercent": 479.68
  }
]
```
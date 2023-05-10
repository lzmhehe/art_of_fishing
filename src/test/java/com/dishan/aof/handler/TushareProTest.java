// package com.dishan.aof.handler;
//
//
// import java.io.IOException;
// import java.util.concurrent.Executors;
// import java.util.concurrent.TimeUnit;
//
// import com.github.tusharepro.core.TusharePro;
// import com.github.tusharepro.core.TushareProService;
// import com.github.tusharepro.core.bean.IndexBasic;
// import com.github.tusharepro.core.bean.StockBasic;
// import com.github.tusharepro.core.common.KeyValue;
// import com.github.tusharepro.core.entity.IndexBasicEntity;
// import com.github.tusharepro.core.entity.IndexDailyEntity;
// import com.github.tusharepro.core.entity.IndexDailybasicEntity;
// import com.github.tusharepro.core.entity.StockBasicEntity;
// import com.github.tusharepro.core.http.Request;
// import lombok.SneakyThrows;
// import okhttp3.MediaType;
// import okhttp3.OkHttpClient;
// import okhttp3.RequestBody;
// import org.junit.Before;
// import org.junit.Test;
//
// public class TushareProTest {
//     final TusharePro.Builder builder = new TusharePro.Builder()
//             .setToken(TOKEN);  // 你的token
//
//     final OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
//             // .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
//             .connectTimeout(42, TimeUnit.SECONDS)
//             .writeTimeout(42, TimeUnit.SECONDS)
//             .readTimeout(42, TimeUnit.SECONDS)
//             .build();
//
//
//     public static final String TOKEN = "494b41c9aaaae68f806a5bc325b7d59b01301a89f091dca93e7c9453";
//
//
//     @Before
//     public void init() {
//         TusharePro.setGlobal(builder.build());  // 设置全局配置
//     }
//
//     @Test
//     public void testApi() {
//         try {
//             final TusharePro.Builder builder = new TusharePro.Builder()
//                     .setToken(TOKEN);  // 你的token
//
//             TusharePro.setGlobal(builder.build());  // 设置全局配置
//
//             final KeyValue<String, String> list_status = StockBasic.Params.list_status.value("L");
//
// // 打印 上海交易所所有上市的沪股通股票 信息
//             TushareProService.stockBasic(new Request<StockBasicEntity>() {
//                     }  // 使用全局配置
//                             .allFields()  // 所有字段
//                             .param(StockBasic.Params.exchange.value("SSE"))  // 参数
//                             .param(list_status)  // 参数
//                             .param("is_hs", "H"))  // 参数
//                     .forEach(System.out::println);
//
//             final OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
//                     // .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
//                     .connectTimeout(42, TimeUnit.SECONDS)
//                     .writeTimeout(42, TimeUnit.SECONDS)
//                     .readTimeout(42, TimeUnit.SECONDS)
//                     .build();
//
// // 一个完整的例子
//             TushareProService.stockBasic(
//                             new Request<StockBasicEntity>(builder.copy()  // 将配置拷贝
//                                     .setMaxRetries(5)  // 设置重试次数, 默认为3
//                                     .setRetrySleepTimeUnit(TimeUnit.SECONDS)  // 设置重试sleep单位, 默认毫秒
//                                     .setRetrySleepTimeOut(60L)  // 设置重试sleep时间, 默认为0
//                                     .setRequestExecutor(Executors.newSingleThreadExecutor((r -> {
//                                         Thread thread = new Thread(r);
//                                         thread.setDaemon(true);
//                                         return thread;
//                                     })))  // 设置请求线程池, 默认CachedThreadPool
//                                     .setHttpFunction(requestBytes -> {  // requestBytes -> function -> responseBytes 请使用阻塞的方式
//                                         try {
//                                             okhttp3.Request request = new okhttp3.Request.Builder()
//                                                     .url("http://api.tushare.pro")
//                                                     .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBytes))
//                                                     .build();
//
//                                             return defaultHttpClient.newCall(request).execute().body().bytes();
//                                         } catch (IOException e) {
//                                             e.printStackTrace();
//                                         }
//                                         return null;
//                                     })
//                                     .build()) {
//                             }
//                                     .allFields()
//                                     .param(StockBasic.Params.exchange.value("SZSE"))
//                                     .param(list_status))
//                     .stream()
//                     .filter(x -> "银行".equals(x.getIndustry()))
//             // .forEach(System.out::println)
//             ;
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
//
//
//     @Test
//     public void testLoadIndexBasic() {
//
//         try {
//             TushareProService.indexBasic(
//                             new Request<IndexBasicEntity>(builder.copy()  // 将配置拷贝
//                                     .setMaxRetries(5)  // 设置重试次数, 默认为3
//                                     .setRetrySleepTimeUnit(TimeUnit.SECONDS)  // 设置重试sleep单位, 默认毫秒
//                                     .setRetrySleepTimeOut(60L)  // 设置重试sleep时间, 默认为0
//                                     .setRequestExecutor(Executors.newSingleThreadExecutor((r -> {
//                                         Thread thread = new Thread(r);
//                                         thread.setDaemon(true);
//                                         return thread;
//                                     })))  // 设置请求线程池, 默认CachedThreadPool
//                                     .setHttpFunction(requestBytes -> {  // requestBytes -> function -> responseBytes 请使用阻塞的方式
//                                         try {
//                                             okhttp3.Request request = new okhttp3.Request.Builder()
//                                                     .url("http://api.tushare.pro")
//                                                     .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBytes))
//                                                     .build();
//
//                                             return defaultHttpClient.newCall(request).execute().body().bytes();
//                                         } catch (IOException e) {
//                                             e.printStackTrace();
//                                         }
//                                         return null;
//                                     })
//                                     .build()) {
//                             }
//
//                                     // 000001.SH   20181018           0.38  11.92
//                                     // 1  000300.SH   20181018           0.27  11.17
//                                     // 2  000905.SH   20181018           0.82  18.03
//                                     // 3  399001.SZ   20181018           0.88  17.48
//                                     // 4  399005.SZ   20181018           0.85  21.43
//                                     // 5  399006.SZ   20181018           1.50  29.56
//                                     // 6  399016.SZ   20181018           1.06  18.86
//                                     // 7  399300.SZ   20181018           0.27  11.17
//                                     .allFields()
//                                     .param(IndexBasic.Params.ts_code.value("000001.SH"))
//                             // .param(StockBasic.Params.exchange.value("SZSE"))
//
//                     ).stream()
//
//                     .forEach(System.out::println);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//         // .param(list_status))
//     }
//
//     @Test
//     @SneakyThrows
//     public void testLoadIndexDaliy() {
//         try {
//             TushareProService.indexDaily(
//
//                             new Request<IndexDailyEntity>(builder.copy()  // 将配置拷贝
//                                     .setMaxRetries(5)  // 设置重试次数, 默认为3
//                                     .setRetrySleepTimeUnit(TimeUnit.SECONDS)  // 设置重试sleep单位, 默认毫秒
//                                     .setRetrySleepTimeOut(60L)  // 设置重试sleep时间, 默认为0
//                                     .setRequestExecutor(Executors.newSingleThreadExecutor((r -> {
//                                         Thread thread = new Thread(r);
//                                         thread.setDaemon(true);
//                                         return thread;
//                                     })))  // 设置请求线程池, 默认CachedThreadPool
//                                     .setHttpFunction(requestBytes -> {  // requestBytes -> function -> responseBytes 请使用阻塞的方式
//                                         try {
//                                             okhttp3.Request request = new okhttp3.Request.Builder()
//                                                     .url("http://api.tushare.pro")
//                                                     .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBytes))
//                                                     .build();
//
//                                             return defaultHttpClient.newCall(request).execute().body().bytes();
//                                         } catch (IOException e) {
//                                             e.printStackTrace();
//                                         }
//                                         return null;
//                                     })
//                                     .build()) {
//                             }
//
//                                     // 000001.SH   20181018           0.38  11.92
//                                     // 1  000300.SH   20181018           0.27  11.17
//                                     // 2  000905.SH   20181018           0.82  18.03
//                                     // 3  399001.SZ   20181018           0.88  17.48
//                                     // 4  399005.SZ   20181018           0.85  21.43
//                                     // 5  399006.SZ   20181018           1.50  29.56
//                                     // 6  399016.SZ   20181018           1.06  18.86
//                                     // 7  399300.SZ   20181018           0.27  11.17
//                                     .allFields()
//                                     .param(IndexDailyEntity.Params.ts_code.value("000001.SH"))
//                             // .param(StockBasic.Params.exchange.value("SZSE"))
//
//
//                     ).stream()
//
//                     .forEach(System.out::println);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
//
//     @Test
//     public void indexDailybasic() {
//         try {
//             TushareProService.indexDailybasic(
//
//                             new Request<IndexDailybasicEntity>(builder.copy()  // 将配置拷贝
//                                     .setMaxRetries(5)  // 设置重试次数, 默认为3
//                                     .setRetrySleepTimeUnit(TimeUnit.SECONDS)  // 设置重试sleep单位, 默认毫秒
//                                     .setRetrySleepTimeOut(60L)  // 设置重试sleep时间, 默认为0
//                                     .setRequestExecutor(Executors.newSingleThreadExecutor((r -> {
//                                         Thread thread = new Thread(r);
//                                         thread.setDaemon(true);
//                                         return thread;
//                                     })))  // 设置请求线程池, 默认CachedThreadPool
//                                     .setHttpFunction(requestBytes -> {  // requestBytes -> function -> responseBytes 请使用阻塞的方式
//                                         try {
//                                             okhttp3.Request request = new okhttp3.Request.Builder()
//                                                     .url("http://api.tushare.pro")
//                                                     .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBytes))
//                                                     .build();
//
//                                             return defaultHttpClient.newCall(request).execute().body().bytes();
//                                         } catch (IOException e) {
//                                             e.printStackTrace();
//                                         }
//                                         return null;
//                                     })
//                                     .build()) {
//                             }
//
//                                     // 000001.SH   20181018           0.38  11.92
//                                     // 1  000300.SH   20181018           0.27  11.17
//                                     // 2  000905.SH   20181018           0.82  18.03
//                                     // 3  399001.SZ   20181018           0.88  17.48
//                                     // 4  399005.SZ   20181018           0.85  21.43
//                                     // 5  399006.SZ   20181018           1.50  29.56
//                                     // 6  399016.SZ   20181018           1.06  18.86
//                                     // 7  399300.SZ   20181018           0.27  11.17
//                                     .allFields()
//                                     .param(IndexDailybasicEntity.Params.ts_code.value("000001.SH"))
//                             // .param(StockBasic.Params.exchange.value("SZSE"))
//                     ).stream()
//
//                     .forEach(System.out::println);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//
//     }
// }
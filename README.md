README
======

This is a small project that uses JDBC & PostgreSQL, and takes advantage of JSON/JSONB feature in PostgreSQL.

It also has a user authentication system.

<br />

PostgreSQL
---------------------------------------------------------
I found it easiest to start with PostgreSQL if you already have a valid docker environment running.

* ```docker run --name pgsql -p 5432:5432 -e POSTGRES_PASSWORD=welcome -v /my/host/path:/var/lib/postgresql/data -d postgres```

This command will
- start a daemon instance (-d) of postgres,
- name it pgsql (--name pgsql),
- open/map default port 5432 (-p 5432:5432),
- map the data drive (-v /my/host/path:/var/lib/postgresql/data),
- use default username postgres,
- and set the password to welcome (-e POSTGRES_PASSWORD=welcome)

Mapping the data drive is needed, so that when we "docker kill" the instance and "docker run" again, as long as we map to the same data drive, the data from last session can survive.

Of course a real installation of PostgreSQL will work just as well.

After the DB is up and running we need to change config.properties to set the JDBC value:
* jdbcUrl = jdbc:postgresql://10.6.11.138/
* jdbcUser = postgres
* jdbcPswd = welcome



JDBC
---------------------------------------------------------
Right now I am using plain old JDBC. In the future we can try JPA/Hibernate too. The focus is on the ability to query JSONB columns.

I found that since a JSONB column query, for example

```
SELECT * FROM cards WHERE data->>'tags' ? 'Clean';
```

uses the question-mark (?), it doesn't work with older JDBC drivers. But the later ones, for example
```
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <version>9.4.1207</version>
</dependency>
```

uses double-question-mark (??) as an escape. So this will work

```
pst = conn.prepareStatement("SELECT * FROM cards WHERE data->'tags' ?? 'Clean'");
```


User password
---------------------------------------------------------
( For reference please se http://jasypt.org/howtoencryptuserpasswords.html )

```	 
 I. Encrypt passwords using one-way techniques, this is, digests.
```
We use HMAC_SHA256 as our main algorithm. It's non-reversible, i.e NO "Retrieve Password". ONLY "Reset Password"

```
II. Match input and stored passwords by comparing digests, not unencrypted strings.
```
Sure.

```
III. Improving the security of our digests, use Variable Salt
```
For each user record we generate and store a different string as encSalt. We use this encSalt to calculate the encPswd, which is what's stored in the user table (together with encSalt).

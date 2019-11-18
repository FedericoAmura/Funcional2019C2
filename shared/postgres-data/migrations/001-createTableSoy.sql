CREATE TABLE public.soy (
    id SERIAL PRIMARY KEY,
    fecha text NOT NULL,
    open double precision NOT NULL,
    high double precision NOT NULL,
    low double precision NOT NULL,
    last double precision NOT NULL,
    cierre double precision NOT NULL,
    ajdif double precision NOT NULL,
    mon text DEFAULT 'D'::text NOT NULL,
    oivol integer NOT NULL,
    oidif integer NOT NULL,
    volope integer NOT NULL,
    unidad text DEFAULT 'TONS'::text NOT NULL,
    dolarbn double precision NOT NULL,
    dolaritau double precision NOT NULL,
    difsem double precision NOT NULL,
    hash integer NOT NULL
);

CREATE UNIQUE INDEX soy_hash_uindex ON public.soy USING btree (hash);
CREATE UNIQUE INDEX soy_id_uindex ON public.soy USING btree (id);

ALTER SEQUENCE soy_id_seq RESTART WITH 3000;
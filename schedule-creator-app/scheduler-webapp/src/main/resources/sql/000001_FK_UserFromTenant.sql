ALTER TABLE shared_users
  ADD CONSTRAINT fk_tenantIdToUser
  FOREIGN KEY (ownertenantid) REFERENCES shared_tenants (id)
if not (args[1] == nil) then
    if not (lunan.addGroup(args[1])) then
        print("error: insufficient permissions")
    end
else
    print("usage: groupadd [GROUP_NAME]")
end
